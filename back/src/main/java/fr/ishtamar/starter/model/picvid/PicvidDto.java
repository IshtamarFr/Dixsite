package fr.ishtamar.starter.model.picvid;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PicvidDto {
    private Long id;
    private String name;
    private Date dateTime;
    private Date dateTimeExif;
    private String takenLocation;
    private String fileLocation;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private Long album_id;
}
