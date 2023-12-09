package gr.aueb.cf.booklibapp.service;

import gr.aueb.cf.booklibapp.auth.AuthenticationResponse;
import gr.aueb.cf.booklibapp.dto.UserDTO;
import gr.aueb.cf.booklibapp.dto.UserLoginDTO;
import gr.aueb.cf.booklibapp.dto.UserRegisterDTO;
import gr.aueb.cf.booklibapp.dto.UserUpdateDTO;
import gr.aueb.cf.booklibapp.model.User;
import reactor.core.publisher.Mono;

import javax.xml.bind.ValidationException;
import java.util.Optional;

public interface IUserService {

    AuthenticationResponse registerUser(UserRegisterDTO userRegisterDto);
    AuthenticationResponse authenticateUser(UserLoginDTO userLoginDto);
    void updateUserDetails(String email, UserUpdateDTO userUpdateDto) throws ValidationException;
    void deleteUserByEmail(String email);
    Mono<Void> addBookToFavorites(String email, String bookId);
    Mono<Void> removeBookFromFavorites(String email, String bookId);
    Optional<User> findByEmail(String email);
    Mono<User> findUserByEmail(String email);
    Optional<User> findUserByEmailWithFavorites(String email);
    UserDTO getUserByEmailWithFavorites(String email);
    boolean hasFavoritedBook(User user, String bookId);
    boolean isEmailTaken(String email);
    boolean isUsernameTaken(String username);
}
