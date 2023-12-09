package gr.aueb.cf.booklibapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookDTO {
    private String id;
    private String title;
    private String smallThumbnail;
}
