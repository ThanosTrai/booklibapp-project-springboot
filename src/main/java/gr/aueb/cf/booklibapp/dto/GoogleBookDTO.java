package gr.aueb.cf.booklibapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoogleBookDTO {
    private String id;
    private VolumeInfoDTO volumeInfo;
}
