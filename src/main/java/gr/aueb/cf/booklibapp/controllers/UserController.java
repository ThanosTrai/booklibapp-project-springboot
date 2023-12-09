package gr.aueb.cf.booklibapp.controllers;

import gr.aueb.cf.booklibapp.dto.*;
import gr.aueb.cf.booklibapp.model.Book;
import gr.aueb.cf.booklibapp.model.User;
import gr.aueb.cf.booklibapp.repository.BookRepository;
import gr.aueb.cf.booklibapp.service.GoogleBooksService;
import gr.aueb.cf.booklibapp.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import javax.xml.bind.ValidationException;
import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserServiceImpl userService;
    private final GoogleBooksService booksService;
    private final BookRepository bookRepository;

    @Autowired
    public UserController(UserServiceImpl userService, GoogleBooksService booksService, BookRepository bookRepository) {
        this.userService = userService;
        this.booksService = booksService;
        this.bookRepository = bookRepository;
    }

    @GetMapping("/{userId}/favorites")
    public ResponseEntity<UserDTO> getCurrentUserWithFavorites(Principal principal) {
        if (principal != null) {
            String email = principal.getName();
            UserDTO userDTO = userService.getUserByEmailWithFavorites(email);

            if (userDTO != null) {
                return ResponseEntity.ok(userDTO);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileDTO> getUserProfile(Principal principal) {
        String email = principal.getName();
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        UserProfileDTO userProfileDto = mapUserToUserProfileDTO(user);

        return ResponseEntity.ok(userProfileDto);
    }


    @PostMapping("/{userId}/favorites/{bookId}")
    public Mono<ResponseEntity<Void>> addBookToFavorites(@PathVariable("userId") String userId, @PathVariable("bookId") String bookId, Principal principal) {
        String userEmail = principal.getName();

        // Fetch book details from Google books API
        Mono<GoogleBookDTO> bookDetailsMono = booksService.findBookById(bookId);

        // Check if the book is favorited by the user
        return Mono.zip(bookDetailsMono, userService.findUserByEmail(userEmail)
                        .map(user -> userService.hasFavoritedBook(user, bookId))
                        .defaultIfEmpty(false))
                .flatMap(tuple -> {
                    GoogleBookDTO book = tuple.getT1();
                    boolean isFavorited = tuple.getT2();

                    if (isFavorited) {
                        // The book is already favorited, return a conflict response
                        return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).<Void>build());
                    } else {
                        // The book is not favorited, proceed to add it to favorites
                        VolumeInfoDTO volumeInfoDTO = book.getVolumeInfo();
                        String bookTitle = volumeInfoDTO.getTitle();
                        String smallThumbnail = volumeInfoDTO.getImageLinks().getSmallThumbnail();

                        // Save the book in database and add it to favorites
                        return Mono.fromCallable(() -> {
                                    Book newBook = new Book();
                                    newBook.setId(book.getId());
                                    newBook.setTitle(bookTitle);
                                    newBook.setSmallThumbnail(smallThumbnail);
                                    return bookRepository.save(newBook);
                                })
                                .flatMap(savedBook -> userService.addBookToFavorites(userEmail, bookId))
                                .thenReturn(ResponseEntity.ok().<Void>build());
                    }
                });
    }

    @DeleteMapping("/{userId}/remove-from-favorites/{bookId}")
    public Mono<ResponseEntity<Void>> removeBookFromFavorites(@PathVariable String userId, @PathVariable String bookId, Principal principal) {
        String userEmail = principal.getName();

        // Check if the user has favorited the book
        return userService.findUserByEmail(userEmail)
                .flatMap(user -> {
                    if (userService.hasFavoritedBook(user, bookId)) {
                        // User has favorited the book, proceed to remove it
                        return userService.removeBookFromFavorites(userEmail, bookId)
                                .then(Mono.just(ResponseEntity.ok().<Void>build()));
                    } else {
                        // User has not favorited the book, return a response indicating that
                        return Mono.just(ResponseEntity.notFound().<Void>build());
                    }
                });
    }


    @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateUserDetails(@Valid @RequestBody UserUpdateDTO userUpdateDTO, BindingResult result, Principal principal) {
        if (result.hasErrors()) {
            Map<String, String> fieldErrors = result.getFieldErrors().stream()
                    .collect(Collectors.toMap(
                            FieldError::getField,
                            fieldError -> Optional.ofNullable(fieldError.getDefaultMessage()).orElse("Default error message")
                    ));

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("message", "Validation failed");
            responseBody.put("errors", fieldErrors);

            return ResponseEntity.badRequest().body(responseBody);
        }

        try {
            String email = principal.getName();
            userService.updateUserDetails(email, userUpdateDTO);
            return ResponseEntity.ok().body(Collections.singletonMap("message", "User profile was updated successfully."));
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("message", "An unexpected error occurred."));
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteUser(Principal principal) {
        try {
            String email = principal.getName();
            userService.deleteUserByEmail(email);
            return ResponseEntity.ok().body(Collections.singletonMap("message", "User account deleted successfully."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("message", "An error occurred while deleting the user account."));
        }
    }

    private UserProfileDTO mapUserToUserProfileDTO(User user) {
        UserProfileDTO userProfileDTO = new UserProfileDTO();
        userProfileDTO.setFirstname(user.getFirstname());
        userProfileDTO.setLastname(user.getLastname());
        userProfileDTO.setDateOfBirth(user.getDateOfBirth());
        userProfileDTO.setProfilePicture(user.getProfilePicture());
        return userProfileDTO;
    }
}
