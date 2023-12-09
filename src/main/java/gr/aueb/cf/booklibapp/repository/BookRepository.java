package gr.aueb.cf.booklibapp.repository;

import gr.aueb.cf.booklibapp.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, String> {
}
