package fr.ishtamar.starter.model.picvid;

import fr.ishtamar.starter.model.album.Album;
import fr.ishtamar.starter.model.comment.Comment;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Picvid {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max=63)
    private String name;

    private Date dateTime;
    private Date dateTimeExif;

    @Size(max=63)
    private String takenLocation;

    @NotNull
    private String fileLocation;

    @Size(max=255)
    private String description;

    @NotNull
    private LocalDateTime createdAt;
    @NotNull
    private LocalDateTime modifiedAt;

    @ManyToOne
    @JoinColumn(name="album_id",referencedColumnName = "id")
    @NotNull
    private Album album;

    @OneToMany(mappedBy ="picvid",cascade = CascadeType.REMOVE,orphanRemoval = true,fetch = FetchType.LAZY)
    List<Comment> comments;

    @NotNull
    private Long fileSize;
}
