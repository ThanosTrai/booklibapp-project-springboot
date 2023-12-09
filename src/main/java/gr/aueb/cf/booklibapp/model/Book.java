package gr.aueb.cf.booklibapp.model;

import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "BOOKS")
public class Book {

    @Id
    @Column(name = "ID", unique = true, nullable = false)
    private String id;

    @Column(name = "TITLE", length = 512, nullable = false)
    private String title;

    @Column(name = "THUMBNAIL_URL", length = 512, nullable = true)
    private String smallThumbnail;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "BOOKS_USERS",
            joinColumns = @JoinColumn(name = "BOOK_ID", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "USER_ID", referencedColumnName = "ID")
    )
    @ToString.Exclude
    private Set<User> favoritedByUsers = new HashSet<>();

    // Helper methods

    public void addFavoritedByUser(User user) {
        if (this.favoritedByUsers == null) {
            this.favoritedByUsers = new HashSet<>();
        }
        this.favoritedByUsers.add(user);
    }

    public void removeFavoritedByUser(User user) {
        if (this.favoritedByUsers != null) {
            this.favoritedByUsers.remove(user);
            user.getFavoriteBooks().remove(this);
        }
    }
}
