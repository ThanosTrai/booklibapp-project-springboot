package gr.aueb.cf.booklibapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VolumeInfoDTO {
    private String title;
    private List<String> authors;
    private String publisher;
    private String publishedDate;
    private String description;
    private List<IndustryIdentifierDTO> industryIdentifiers;
    private Integer pageCount;
    private List<String> categories;
    private ImageLinksDTO imageLinks;

    public String getIsbn13() {
        if (industryIdentifiers != null) {
            return industryIdentifiers.stream()
                    .filter(id -> "ISBN_13".equals(id.getType()))
                    .findFirst()
                    .map(IndustryIdentifierDTO::getIdentifier)
                    .orElse(null);
        }
        return null;
    }
}
