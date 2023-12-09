package gr.aueb.cf.booklibapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoogleBookDetailsDTO {
    private GoogleBookDTO book;
    private boolean favorited;
}
