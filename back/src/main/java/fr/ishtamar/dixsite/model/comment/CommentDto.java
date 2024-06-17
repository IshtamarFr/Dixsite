package fr.ishtamar.dixsite.model.comment;

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
public class CommentDto {
    private Long id;
    private String content;
    private String status;
    private LocalDateTime createdAt;
    private Long owner_id;
    private String owner_name;
    private Long picvid_id;
    private Long album_id;
    private Long mother_id;
    private List<CommentDto> subcomments;
}
