package gr.aueb.cf.booklibapp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import gr.aueb.cf.booklibapp.auth.AuthenticationResponse;
import gr.aueb.cf.booklibapp.dto.*;
import gr.aueb.cf.booklibapp.model.*;
import gr.aueb.cf.booklibapp.repository.BookRepository;
import gr.aueb.cf.booklibapp.repository.TokenRepository;
import gr.aueb.cf.booklibapp.repository.UserRepository;
import gr.aueb.cf.booklibapp.security.jwt.JwtService;
import gr.aueb.cf.booklibapp.service.exceptions.AuthenticationFailedException;
import gr.aueb.cf.booklibapp.service.exceptions.PasswordMismatchException;
import gr.aueb.cf.booklibapp.service.exceptions.UserAlreadyExistsException;
import gr.aueb.cf.booklibapp.service.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.ValidationException;
import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, BookRepository bookRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager, TokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.tokenRepository = tokenRepository;
    }

    @Transactional
    @Override
    public AuthenticationResponse registerUser(UserRegisterDTO userRegisterDto) {
        if (userRepository.usernameExists(userRegisterDto.getUsername()) || userRepository.emailExists(userRegisterDto.getEmail())) {
            throw new UserAlreadyExistsException("User with the given username or email already exists");
        }
        if (!userRegisterDto.getPassword().equals(userRegisterDto.getConfirmPassword())) {
            throw new PasswordMismatchException("Passwords do not match!");
        }
        var user = User.builder()
                .username(userRegisterDto.getUsername())
                .email(userRegisterDto.getEmail())
                .password(passwordEncoder.encode(userRegisterDto.getPassword()))
                .role(Role.USER)
                .build();
        var savedUser = userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        saveUserToken(savedUser, jwtToken);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional
    @Override
    public AuthenticationResponse authenticateUser(UserLoginDTO loginDTO) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDTO.getEmail(),
                            loginDTO.getPassword()
                    )
            );
            var user = userRepository.findByEmail(loginDTO.getEmail())
                    .orElseThrow(() -> new UserNotFoundException("User with email " + loginDTO.getEmail() + " not found"));

            var jwtToken = jwtService.generateTokenWithUserId(user, user.getId());
            var refreshToken = jwtService.generateRefreshToken(user);
            revokeAllUserTokens(user);
            saveUserToken(user, jwtToken);
            return AuthenticationResponse.builder()
                    .accessToken(jwtToken)
                    .refreshToken(refreshToken).build();
        } catch (AuthenticationException e) {
            throw new AuthenticationFailedException("Invalid email or password");
        }
    }

    @Transactional
    @Override
    public void updateUserDetails(String email, UserUpdateDTO userUpdateDto) throws ValidationException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Update field only if it's provided
        if (StringUtils.hasText(userUpdateDto.getFirstname())) {
            user.setFirstname(userUpdateDto.getFirstname());
        }
        if (StringUtils.hasText(userUpdateDto.getLastname())) {
            user.setLastname(userUpdateDto.getLastname());
        }
        if (userUpdateDto.getDateOfBirth() != null) {
            user.setDateOfBirth(userUpdateDto.getDateOfBirth());
        }
        if (userUpdateDto.getProfilePicture() != null) {
            user.setProfilePicture(userUpdateDto.getProfilePicture());
        } else {
            user.setProfilePicture(null);
        }
        // Validate and update password only if it's provided
        if (StringUtils.hasText(userUpdateDto.getPassword())) {
            validatePassword(userUpdateDto.getPassword());
            user.setPassword(passwordEncoder.encode(userUpdateDto.getPassword()));
        }
        userRepository.save(user);
    }

    @Transactional
    @Override
    public void deleteUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        userRepository.delete(user);
    }

    @Transactional
    @Override
    public Mono<Void> addBookToFavorites(String email, String bookId) {
        return Mono.fromCallable(() -> {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));
            Book book = bookRepository.findById(bookId)
                    .orElseThrow(() -> new EntityNotFoundException("Book not found."));
            user.addFavoriteBook(book);
            book.addFavoritedByUser(user);
            userRepository.save(user);
            return null;
        });
    }

    @Transactional
    @Override
    public Mono<Void> removeBookFromFavorites(String email, String bookId) {
        return Mono.fromRunnable(() -> {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new EntityNotFoundException("User not found."));
            Book book = bookRepository.findById(bookId)
                    .orElseThrow(() -> new EntityNotFoundException("Book not found."));

            if (user.getFavoriteBooks().contains(book)) {
                user.removeFavoriteBook(book);
            }
            if (book.getFavoritedByUsers().contains(user)) {
                book.removeFavoritedByUser(user);
            }
            userRepository.saveAndFlush(user);
            bookRepository.saveAndFlush(book);
        }).then();
    }

    @Override
    public boolean hasFavoritedBook(User user, String bookId) {
        return user.getFavoriteBooks().stream()
                .anyMatch(book -> book.getId().equals(bookId));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> findUserByEmailWithFavorites(String email) {
        return userRepository.findByEmailWithFavorites(email);
    }

    @Override
    public Mono<User> findUserByEmail(String email) {
        return Mono.fromCallable(() -> findUserByEmailWithFavorites(email))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(userOpt -> userOpt.map(Mono::just).orElseGet(Mono::empty));
    }

    @Override
    public UserDTO getUserByEmailWithFavorites(String email) {
        Optional<User> userOpt = userRepository.findByEmailWithFavorites(email);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            UserDTO userDTO = convertToUserDTO(user);
            return userDTO;
        }
        return null;
    }

    @Override
    public boolean isEmailTaken(String email) {
        return userRepository.emailExists(email);
    }

    @Override
    public boolean isUsernameTaken(String username) {
        return userRepository.usernameExists(username);
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            var user = this.userRepository.findByEmail(userEmail)
                    .orElseThrow();
            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                var authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }

    private UserDTO convertToUserDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());

        Set<BookDTO> bookDtoSet = user.getFavoriteBooks().stream()
                .map(this::convertToBookDTO)
                .collect(Collectors.toSet());

        dto.setFavoriteBooks(bookDtoSet);
        return dto;
    }

    private BookDTO convertToBookDTO(Book book) {
        BookDTO dto = new BookDTO();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setSmallThumbnail(book.getSmallThumbnail());
        return dto;
    }

    private void validatePassword(String password) throws ValidationException {
        String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_]).{6,}$";
        if (!password.matches(passwordPattern)) {
            throw new ValidationException("Password must contain at least one lowercase, one uppercase, one digit, and one special character.");
        }
    }
}
