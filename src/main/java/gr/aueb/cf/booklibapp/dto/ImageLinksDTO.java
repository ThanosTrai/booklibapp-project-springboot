package gr.aueb.cf.booklibapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageLinksDTO {

    private String smallThumbnail;
    private String thumbnail;

    public String getBestAvailableImage() {
        return (thumbnail != null && !thumbnail.isEmpty() ? thumbnail : smallThumbnail);
    }
}
