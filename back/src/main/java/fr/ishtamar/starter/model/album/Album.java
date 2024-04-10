package fr.ishtamar.starter.model.album;

import fr.ishtamar.starter.model.picvid.Picvid;
import fr.ishtamar.starter.model.picvid.PicvidRepository;
import fr.ishtamar.starter.model.user.UserInfo;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Album {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(max=63)
    private String name;

    @NotNull
    @Size(max=255)
    private String description;

    private String homePicture;

    @NotNull
    private String status;

    @ManyToOne
    @JoinColumn(name="owner_id",referencedColumnName = "id")
    private UserInfo owner;

    @ManyToMany
    @JoinTable(name="user_album_subscription", joinColumns= @JoinColumn(name="album_id"),inverseJoinColumns = @JoinColumn(name="user_id"))
    private List<UserInfo> subscribers;

    @ManyToMany
    @JoinTable(name="user_album_moderation", joinColumns= @JoinColumn(name="album_id"),inverseJoinColumns = @JoinColumn(name="user_id"))
    private List<UserInfo> moderators;

    @NotNull
    private LocalDateTime createdAt;

    @NotNull
    private LocalDateTime modifiedAt;

    @OneToMany(mappedBy = "album",cascade = CascadeType.REMOVE)
    private List<Picvid> picvids;

    private Long quota;

    public Long getTotalSize() {
        return picvids != null ? picvids.stream().mapToLong(Picvid::getFileSize).sum() : 0L;
    }
}
