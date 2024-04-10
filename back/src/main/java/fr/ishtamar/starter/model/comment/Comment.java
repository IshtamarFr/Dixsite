package fr.ishtamar.starter.model.comment;

import fr.ishtamar.starter.model.picvid.Picvid;
import fr.ishtamar.starter.model.user.UserInfo;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max=255)
    private String content;

    @NotNull
    private String status;

    @NotNull
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name="author_id",referencedColumnName = "id")
    @NotNull
    private UserInfo owner;

    @ManyToOne
    @JoinColumn(name="picvid_id",referencedColumnName = "id")
    @NotNull
    private Picvid picvid;

    @ManyToOne
    @JoinColumn(name="mother_id",referencedColumnName = "id")
    private Comment mother;

    @OneToMany(mappedBy = "mother")
    private List<Comment> children;
}
