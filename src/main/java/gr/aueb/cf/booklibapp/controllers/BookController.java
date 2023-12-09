package gr.aueb.cf.booklibapp.controllers;

import gr.aueb.cf.booklibapp.dto.GoogleBookDTO;
import gr.aueb.cf.booklibapp.dto.GoogleBookDetailsDTO;
import gr.aueb.cf.booklibapp.service.GoogleBooksService;
import gr.aueb.cf.booklibapp.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.Principal;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final GoogleBooksService googleBooksService;
    private final UserServiceImpl userService;

    @Autowired
    public BookController(GoogleBooksService googleBooksService, UserServiceImpl userService) {
        this.googleBooksService = googleBooksService;
        this.userService = userService;
    }

    @GetMapping("/search")
    public Flux<GoogleBookDTO> searchBooks(@RequestParam String query) {
        return googleBooksService.searchBooks(query);
    }

    @GetMapping("/search-by-title")
    public Flux<GoogleBookDTO> searchBooksByTitle(@RequestParam String title) {
        return googleBooksService.searchBooksByTitle(title);
    }

    @GetMapping("/search-by-author")
    public Flux<GoogleBookDTO> searchBooksByAuthor(@RequestParam String author) {
        return googleBooksService.searchBooksByAuthor(author);
    }

    @GetMapping("/search-by-category")
    public Flux<GoogleBookDTO> searchBooksByCategory(@RequestParam String category) {
        return googleBooksService.searchBooksByCategory(category);
    }


    @GetMapping("/search-by-isbn")
    public Mono<ResponseEntity<?>> searchBooksByIsbn(@RequestParam String isbn) {
        return googleBooksService.searchBooksByIsbn(isbn)
                .collectList()
                .flatMap(books -> {
                    if (books.isEmpty()) {
                        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body("No books found with the given ISBN."));
                    } else {
                        return Mono.just(ResponseEntity.ok(books));
                    }
                })
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).body("No books found with the given ISBN."));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<GoogleBookDetailsDTO>> findBookByIdAndCheckFavorited(
            @PathVariable String id, Principal principal) {

        Mono<GoogleBookDTO> bookMono = googleBooksService.findBookById(id);

        Mono<Boolean> favoritedMono = userService.findUserByEmail(principal.getName())
                .map(user -> user.hasFavoritedBook(id))
                .defaultIfEmpty(false);

        return Mono.zip(bookMono, favoritedMono, GoogleBookDetailsDTO::new)
                .map(detailsDto -> ResponseEntity.ok().body(detailsDto))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

}
