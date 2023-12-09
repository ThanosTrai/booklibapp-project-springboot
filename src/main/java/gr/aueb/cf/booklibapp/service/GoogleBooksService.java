package gr.aueb.cf.booklibapp.service;

import gr.aueb.cf.booklibapp.dto.GoogleBookDTO;
import gr.aueb.cf.booklibapp.dto.GoogleBooksSearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class GoogleBooksService {

    private final WebClient webClient;

    @Value("${google.books.apiKey}")
    private String apiKey;

    @Autowired
    public GoogleBooksService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Flux<GoogleBookDTO> searchBooks(String query) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/volumes")
                        .queryParam("q", query)
                        .queryParam("key", apiKey)
                        .queryParam("printType", "books")
                        .queryParam("langRestrict", "en")
                        .queryParam("maxResults", 18)
                        .build())
                .retrieve()
                .bodyToMono(GoogleBooksSearchResponse.class) // Deserialize into the wrapper DTO
                .flatMapMany(response -> {
                    if (response.getItems() == null) {
                        return Flux.empty();
                    }
                   return Flux.fromIterable(response.getItems()); // Extract the list of items
                });
    }

    public Flux<GoogleBookDTO> searchBooksByTitle(String title) {
        return searchBooks("intitle:" + title);
    }

    public Flux<GoogleBookDTO> searchBooksByAuthor(String author) {
        return searchBooks("inauthor:" + author);
    }

    public Flux<GoogleBookDTO> searchBooksByCategory(String category) {
        return searchBooks("subject:" + category);
    }

    public Flux<GoogleBookDTO> searchBooksByIsbn(String isbn) {
        return searchBooks("isbn:" + isbn);
    }

    public Mono<GoogleBookDTO> findBookById(String id) {
        return webClient.get()
                .uri("/volumes/{id}", id)
                .retrieve()
                .bodyToMono(GoogleBookDTO.class); //Single book resource
    }
}
