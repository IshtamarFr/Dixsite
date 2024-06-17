package fr.ishtamar.dixsite.model.picvid;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ModifyPicvidRequest {
    @Size(max=63)
    private String name;

    private Date date;
    private boolean isDateDeleted;

    @Size(max=63)
    private String takenLocation;

    @Size(max=255)
    private String description;
}

