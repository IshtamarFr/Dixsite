package fr.ishtamar;

import fr.ishtamar.dixsite.model.album.Album;
import fr.ishtamar.dixsite.model.comment.Comment;
import fr.ishtamar.dixsite.model.picvid.Picvid;
import fr.ishtamar.starter.model.user.UserInfo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static fr.ishtamar.starter.security.SecurityConfig.passwordEncoder;

public class TestContent {
    public UserInfo initialUser=UserInfo.builder()
            .name("Ishta")
            .email("test@test.com")
            .password(passwordEncoder().encode("123456"))
            .roles("ROLE_USER")
            .maxAlbums(10)
            .build();

    public UserInfo initialUser2=UserInfo.builder()
            .name("Pal")
            .email("test17@test.com")
            .password(passwordEncoder().encode("654321"))
            .roles("ROLE_USER")
            .maxAlbums(10)
            .build();

    public UserInfo initialAdmin=UserInfo.builder()
            .name("Me")
            .email("admin@test.com")
            .password(passwordEncoder().encode("654321"))
            .roles("ROLE_ADMIN")
            .maxAlbums(10)
            .build();

    public Album initialAlbum=Album.builder()
            .name("Dixee")
            .owner(initialUser)
            .description("La plus belle")
            .status("ONLINE")
            .createdAt(LocalDateTime.now())
            .modifiedAt(LocalDateTime.now())
            .build();

    public Album initialAlbum2=Album.builder()
            .name("Ozie")
            .owner(initialUser2)
            .description("On ne l'aime pas")
            .status("ONLINE")
            .createdAt(LocalDateTime.now())
            .modifiedAt(LocalDateTime.now())
            .subscribers(new ArrayList<>())
            .build();

    public Album initialAlbum3=Album.builder()
            .name("Freud")
            .owner(initialUser)
            .description("R.I.P")
            .status("ONLINE")
            .createdAt(LocalDateTime.now())
            .modifiedAt(LocalDateTime.now())
            .build();

    public Picvid initialPicvid=Picvid.builder()
            .album(initialAlbum)
            .createdAt(LocalDateTime.now())
            .modifiedAt(LocalDateTime.now())
            .fileLocation("AaAaAaAa")
            .fileSize(123456L)
            .build();

    public Comment initialComment1=Comment.builder()
            .content("Super commentaire")
            .picvid(initialPicvid)
            .owner(initialUser)
            .createdAt(LocalDateTime.now())
            .status("ONLINE")
            .build();

    public Comment initialComment2=Comment.builder()
            .content("Je n'aime pas")
            .picvid(initialPicvid)
            .owner(initialUser2)
            .createdAt(LocalDateTime.now())
            .status("ONLINE")
            .build();

    public Comment initialAnswer=Comment.builder()
            .content("Je n'aime pas")
            .picvid(initialPicvid)
            .owner(initialUser)
            .mother(initialComment2)
            .createdAt(LocalDateTime.now())
            .status("ONLINE")
            .build();
}
