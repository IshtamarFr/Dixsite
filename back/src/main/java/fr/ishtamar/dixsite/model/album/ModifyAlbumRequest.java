package fr.ishtamar.dixsite.model.album;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ModifyAlbumRequest {
    @NotNull
    @Size(max=63)
    private String name;

    @NotNull
    @Size(max=255)
    private String description;

    private MultipartFile homePicture;
    private String isHomePictureEmpty; //needed for frontEnd, expected 'true' or 'false'
}
