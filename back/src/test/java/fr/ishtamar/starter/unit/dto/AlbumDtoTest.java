package fr.ishtamar.starter.unit.dto;

import fr.ishtamar.starter.model.album.Album;
import fr.ishtamar.starter.model.album.AlbumMapper;
import fr.ishtamar.starter.model.picvid.Picvid;
import fr.ishtamar.starter.model.user.UserInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;

import static fr.ishtamar.starter.security.SecurityConfig.passwordEncoder;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class AlbumDtoTest {
    @Autowired
    AlbumMapper mapper;

    final static UserInfo initialUser=UserInfo.builder()
            .id(654L)
            .name("TickleMonster")
            .email("test999@test.com")
            .password(passwordEncoder().encode("Aa1234567!"))
            .roles("ROLE_USER")
            .build();

    final Album initialAlbum=Album.builder()
            .id(456L)
            .name("Dixee")
            .owner(initialUser)
            .description("La plus belle")
            .status("ONLINE")
            .createdAt(LocalDateTime.now())
            .modifiedAt(LocalDateTime.now())
            .build();

    final Picvid picvid1=Picvid.builder()
            .id(852L)
            .album(initialAlbum)
            .createdAt(LocalDateTime.now())
            .modifiedAt(LocalDateTime.now())
            .name("photo de Dixee")
            .description("On voit Dixee sourire")
            .fileLocation("AaAaAa.jpg")
            .build();

    final Picvid picvid2=Picvid.builder()
            .id(963L)
            .album(initialAlbum)
            .createdAt(LocalDateTime.now())
            .modifiedAt(LocalDateTime.now())
            .name("photo de Ozie")
            .description("Elle mord les gens !")
            .fileLocation("BBBBBBBB.jpg")
            .build();

    @Test
    @DisplayName("When I try to get an AlbumDto from Album with no picvid, all is OK")
    void testAlbumToDtoWithoutPicvid() throws Exception {
        //Given

        //When
        String dto=mapper.toDto(initialAlbum).toString();

        //Then
        assertThat(dto).contains("owner_id=654");
        assertThat(dto).contains("Tickle");
    }

    @Test
    @DisplayName("When I try to get an AlbumDto from Album with picvids, all is OK")
    void testAlbumToDtoWithPicvid() throws Exception {
        //Given
        initialAlbum.setPicvids(List.of(picvid1,picvid2));

        //When-Then
        assertThat(mapper.toDto(initialAlbum).toString()).contains("picvid_ids=[852, 963]");
    }
}
