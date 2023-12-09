package gr.aueb.cf.booklibapp.repository;

import gr.aueb.cf.booklibapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.favoriteBooks WHERE u.email = ?1")
    Optional<User> findByEmailWithFavorites(@Param("email") String email);

    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.username = ?1")
    boolean usernameExists(String username);

    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.email = ?1")
    boolean emailExists(String email);

}
