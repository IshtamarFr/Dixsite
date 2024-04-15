package fr.ishtamar.starter.model.picvid;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreatePicvidRequest {
    @Size(max=63)
    private String name;

    private Date date;

    @NotNull
    private MultipartFile picvid;

    @Size(max=63)
    private String takenLocation;

    @Size(max=255)
    private String description;
}
