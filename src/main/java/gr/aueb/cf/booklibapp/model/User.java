package gr.aueb.cf.booklibapp.model;

import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "USERS")
public class User implements UserDetails {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "USERNAME", nullable = false, unique = true)
    private String username;

    @Column(name = "FIRSTNAME", nullable = true)
    private String firstname;

    @Column(name = "LASTNAME", nullable = true)
    private String lastname;

    @Column(name = "EMAIL", nullable = false, unique = true)
    private String email;

    @Column(name = "PASSWORD", nullable = false)
    private String password;

    @Column(name = "DATE_OF_BIRTH", nullable = true)
    private LocalDate dateOfBirth;

    @Column(name = "PROFILE_PICTURE", nullable = true)
    private String profilePicture;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user", cascade = CascadeType.ALL)
    private List<Token> tokens;

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "favoritedByUsers", cascade = CascadeType.ALL)
    private Set<Book> favoriteBooks = new HashSet<>();

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof User)) return false;
        User user = (User) obj;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public String getUsername() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    // Helper methods

    public void addFavoriteBook(Book book) {
        if (this.favoriteBooks == null) {
            this.favoriteBooks = new HashSet<>();
        }

        if (!this.favoriteBooks.contains(book)) {
            this.favoriteBooks.add(book);
            book.getFavoritedByUsers().add(this);
        }
    }

    public void removeFavoriteBook(Book book) {
        if (this.favoriteBooks != null && this.favoriteBooks.contains(book)) {
            this.favoriteBooks.remove(book);
            book.removeFavoritedByUser(this);
        }
    }

    public boolean hasFavoritedBook(String bookId) {
        return this.favoriteBooks != null && this.favoriteBooks.stream()
                .anyMatch(book -> book.getId().equals(bookId));
    }
}
