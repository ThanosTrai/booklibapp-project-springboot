package gr.aueb.cf.booklibapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webApiClient() { return WebClient.create("https://www.googleapis.com/books/v1/"); }
}
