package fr.ishtamar.starter.model.album;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AlbumDto {
    private Long id;
    private String name;
    private String description;
    private String homePicture;
    private String status;
    private Long owner_id;
    private String owner_name;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private List<Long> picvid_ids;
    private Long quota;
    private List<Long> moderator_ids;
    private List<String> moderator_names;
}
