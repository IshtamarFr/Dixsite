package fr.ishtamar.starter.model.user;

import fr.ishtamar.starter.model.album.Album;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(uniqueConstraints={
        @UniqueConstraint(columnNames = {"name"}),
        @UniqueConstraint(columnNames = {"email"})
})
public class UserInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(max=30)
    private String name;

    @NotNull
    @Size(max=63)
    @Email
    private String email;

    @NotNull
    @Size(max=60)
    private String password;

    @NotNull
    private String roles;

    @Size(max=15)
    private String token;

    @OneToMany(mappedBy="owner")
    private List<Album> ownedAlbums;

    @ManyToMany(mappedBy="subscribers")
    private List<Album> subscribedAlbums;

    @ManyToMany(mappedBy="moderators")
    private List<Album> moderatedAlbums;

    private Integer maxAlbums;
}
