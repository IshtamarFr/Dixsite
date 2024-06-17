package fr.ishtamar.dixsite;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.ishtamar.dixsite.model.album.Album;
import fr.ishtamar.dixsite.model.album.AlbumRepository;
import fr.ishtamar.dixsite.model.album.AlbumService;
import fr.ishtamar.dixsite.model.album.AlbumStatusChangeRequest;
import fr.ishtamar.starter.filetransfer.FileUploadServiceImpl;
import fr.ishtamar.starter.security.JwtService;
import fr.ishtamar.starter.model.user.UserInfo;
import fr.ishtamar.starter.model.user.UserInfoRepository;
import org.hamcrest.Matchers;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static fr.ishtamar.starter.security.SecurityConfig.passwordEncoder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AlbumControllerIT {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    AlbumRepository repository;
    @Autowired
    UserInfoRepository userInfoRepository;
    @Autowired
    JwtService jwtService;
    @Autowired
    AlbumService albumService;
    @MockBean
    FileUploadServiceImpl fileUploadServiceImpl;

    final ObjectMapper mapper=new ObjectMapper();

    final UserInfo initialUser=UserInfo.builder()
            .name("Ishta")
            .email("test@test.com")
            .password(passwordEncoder().encode("123456"))
            .roles("ROLE_USER")
            .maxAlbums(10)
            .build();

    final UserInfo initialUser2=UserInfo.builder()
            .name("Pal")
            .email("test17@test.com")
            .password(passwordEncoder().encode("654321"))
            .roles("ROLE_USER")
            .maxAlbums(10)
            .build();

    final UserInfo initialAdmin=UserInfo.builder()
            .name("Ed")
            .email("adm@test.com")
            .password(passwordEncoder().encode("Aa123456"))
            .roles("ROLE_ADMIN")
            .maxAlbums(10)
            .build();

    final Album initialAlbum=Album.builder()
            .name("Dixee")
            .owner(initialUser)
            .description("La plus belle")
            .status("ONLINE")
            .createdAt(LocalDateTime.now())
            .modifiedAt(LocalDateTime.now())
            .moderators(List.of())
            .build();

    final Album initialAlbum2=Album.builder()
            .name("Ozie")
            .owner(initialUser2)
            .description("On ne l'aime pas")
            .status("ONLINE")
            .createdAt(LocalDateTime.now())
            .modifiedAt(LocalDateTime.now())
            .subscribers(new ArrayList<>())
            .build();

    final Album initialAlbum3=Album.builder()
            .name("Freud")
            .owner(initialUser)
            .description("R.I.P")
            .status("ONLINE")
            .createdAt(LocalDateTime.now())
            .modifiedAt(LocalDateTime.now())
            .build();

    @AfterEach
    void clean() {
        repository.deleteAll();
        userInfoRepository.deleteAll();
    }

    @Test
    @DisplayName("When I want the list of albums for a user, I get it")
    @WithMockUser(roles="USER")
    void testGetAllAlbumsForUser() throws Exception {
        //Given
        Long id=userInfoRepository.save(initialUser).getId();
        userInfoRepository.save(initialUser2);
        repository.save(initialAlbum);
        repository.save(initialAlbum2);
        repository.save(initialAlbum3);

        //When
        mockMvc.perform(get("/user/"+id+"/album"))
                .andDo(print())

        //Then
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Dixee")))
                .andExpect(content().string(containsString("Freud")))
                .andExpect(content().string(containsString("plus belle")))
                .andExpect(content().string(Matchers.not(containsString("Ozie"))));
    }

    @Test
    @DisplayName("When I want the list of albums for an invalid user, I get a bad request")
    @WithMockUser(roles="USER")
    void testGetAllAlbumsForInvalidUser() throws Exception {
        //Given
        userInfoRepository.save(initialUser);
        userInfoRepository.save(initialUser2);
        repository.save(initialAlbum);
        repository.save(initialAlbum2);
        repository.save(initialAlbum3);

        //When
        mockMvc.perform(get("/user/0/album"))

                //Then
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("When I try to create a valid new album, all is OK")
    @WithMockUser(roles="USER")
    void testPostNewAlbum() throws Exception {
        //Given
        Long id=userInfoRepository.save(initialUser).getId();
        String jwt=jwtService.generateToken(initialUser.getEmail());

        //When
        mockMvc.perform(post("/user/"+id+"/album")
                        .header("Authorization","Bearer "+jwt)
                        .param("name", "SCP999")
                        .param("description", "Il est sucré")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
        //Then
                .andExpect(status().isOk());
        assertThat(repository.findAll().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("When I try to create an invalid new album, it is bad request")
    @WithMockUser(roles="USER")
    void testPostNewInvalidAlbum() throws Exception {
        //Given
        Long id=userInfoRepository.save(initialUser).getId();
        String jwt=jwtService.generateToken(initialUser.getEmail());

        //When
        mockMvc.perform(post("/user/"+id+"/album")
                        .header("Authorization","Bearer "+jwt)
                        .param("description", "Il est sucré")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                //Then
                .andExpect(status().isBadRequest());
        assertThat(repository.findAll().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("When I try to create an invalid new album, it is bad request")
    @WithMockUser(roles="USER")
    void testPostNewAlbumWithNonOwner() throws Exception {
        //Given
        long id=userInfoRepository.save(initialUser).getId()+1234;
        String jwt=jwtService.generateToken(initialUser.getEmail());

        //When
        mockMvc.perform(post("/user/"+id+"/album")
                        .header("Authorization","Bearer "+jwt)
                        .param("name", "SCP999")
                        .param("description", "Il est sucré")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                //Then
                .andExpect(status().isBadRequest());
        assertThat(repository.findAll().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("When I try to subscribe to someone else's album, it is OK")
    @WithMockUser(roles="USER")
    void testSubscribeToAlbum() throws Exception {
        //Given
        Long id=userInfoRepository.save(initialUser).getId();
        String jwt=jwtService.generateToken(initialUser.getEmail());

        userInfoRepository.save(initialUser2);
        Long id2=repository.save(initialAlbum2).getId();

        //When
        mockMvc.perform(post("/user/"+id+"/album/"+id2+"/subscribe")
                .header("Authorization","Bearer "+jwt))

        //Then
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("When I try to subscribe to someone else's album Im already subscribed to, it is Bad Request")
    @WithMockUser(roles="USER")
    void testReSubscribeToAlbum() throws Exception {
        //Given
        Long id=userInfoRepository.save(initialUser).getId();
        String jwt=jwtService.generateToken(initialUser.getEmail());

        userInfoRepository.save(initialUser2);
        initialAlbum2.getSubscribers().add(initialUser);
        Long id2=repository.save(initialAlbum2).getId();


        //When
        mockMvc.perform(post("/user/"+id+"/album/"+id2+"/subscribe")
                        .header("Authorization","Bearer "+jwt))

                //Then
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("When I try to unsubscribe to someone else's album, it is OK")
    @WithMockUser(roles="USER")
    void testUnSubscribeToAlbum() throws Exception {
        //Given
        Long id=userInfoRepository.save(initialUser).getId();
        String jwt=jwtService.generateToken(initialUser.getEmail());

        userInfoRepository.save(initialUser2);
        initialAlbum2.getSubscribers().add(initialUser);
        Long id2=repository.save(initialAlbum2).getId();

        //When
        mockMvc.perform(delete("/user/"+id+"/album/"+id2+"/subscribe")
                        .header("Authorization","Bearer "+jwt))

                //Then
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("When I try to unsubscribe to someone else's album Im not subscribed to, it is Bad Request")
    @WithMockUser(roles="USER")
    void testUnSubscribeToAlbumWhenNotSubscribed() throws Exception {
        //Given
        Long id=userInfoRepository.save(initialUser).getId();
        String jwt=jwtService.generateToken(initialUser.getEmail());

        userInfoRepository.save(initialUser2);
        Long id2=repository.save(initialAlbum2).getId();


        //When
        mockMvc.perform(delete("/user/"+id+"/album/"+id2+"/subscribe")
                        .header("Authorization","Bearer "+jwt))

                //Then
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("When I try to OFFLINE my own album (not moderated), it works")
    @WithMockUser(roles="USER")
    void testOfflineMyAlbum() throws Exception {
        //Given
        Long id=userInfoRepository.save(initialUser2).getId();
        String jwt=jwtService.generateToken(initialUser2.getEmail());

        Long id2=repository.save(initialAlbum2).getId();
        AlbumStatusChangeRequest request=AlbumStatusChangeRequest.builder()
                .action("OFFLINE")
                .build();

        //When
        mockMvc.perform(put("/user/"+id+"/album/"+id2+"/status")
                .header("Authorization","Bearer "+jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))

        //Then
                .andExpect(status().isOk());
        assertThat(repository.findById(id2).get().getStatus()).isEqualTo("OFFLINE");
    }

    @Test
    @DisplayName("When I try to ONLINE my own album , it is bad request")
    @WithMockUser(roles="USER")
    void testOnlineMyModeratedAlbum() throws Exception {
        //Given
        Long id = userInfoRepository.save(initialUser2).getId();
        String jwt = jwtService.generateToken(initialUser2.getEmail());

        initialAlbum2.setStatus("MODERATED");
        Long id2 = repository.save(initialAlbum2).getId();
        AlbumStatusChangeRequest request = AlbumStatusChangeRequest.builder()
                .action("ONLINE")
                .build();

        //When
        mockMvc.perform(put("/user/" + id + "/album/" + id2 + "/status")
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))

                //Then
                .andExpect(status().isBadRequest());
        assertThat(repository.findById(id2).get().getStatus()).isEqualTo("MODERATED");
    }

    @Test
    @DisplayName("When Admin try to ONLINE a MODERATED album , it is OK")
    @WithMockUser(roles="ADMIN")
    void testOnlineModeratedAlbumFromAdmin() throws Exception {
        //Given
        Long id=userInfoRepository.save(initialAdmin).getId();
        String jwt=jwtService.generateToken(initialAdmin.getEmail());

        userInfoRepository.save(initialUser2);
        initialAlbum2.setStatus("MODERATED");
        Long id2=repository.save(initialAlbum2).getId();
        AlbumStatusChangeRequest request=AlbumStatusChangeRequest.builder()
                .action("ONLINE")
                .build();

        //When
        mockMvc.perform(put("/user/"+id+"/album/"+id2+"/status")
                        .header("Authorization","Bearer "+jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))

                //Then
                .andExpect(status().isOk());
        assertThat(repository.findById(id2).get().getStatus()).isEqualTo("ONLINE");
    }

    @Test
    @DisplayName("When Admin has a wrong jwt , it is bad credentials")
    @WithMockUser(roles="ADMIN")
    void testAdminChangeStatusWithWrongJwt() throws Exception {
        //Given
        Long id=userInfoRepository.save(initialAdmin).getId();
        userInfoRepository.save(initialUser2);
        String jwt=jwtService.generateToken(initialUser2.getEmail());

        initialAlbum2.setStatus("MODERATED");
        Long id2=repository.save(initialAlbum2).getId();
        AlbumStatusChangeRequest request=AlbumStatusChangeRequest.builder()
                .action("ONLINE")
                .build();

        //When
        mockMvc.perform(put("/user/"+id+"/album/"+id2+"/status")
                        .header("Authorization","Bearer "+jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))

                //Then
                .andExpect(status().isBadRequest());
        assertThat(repository.findById(id2).get().getStatus()).isEqualTo("MODERATED");
    }

    @Test
    @DisplayName("When Admin wants to get all albums, he get them all (offline, moderated,etc.)")
    @WithMockUser(roles="ADMIN")
    void testGetAllAlbumsAsAdmin() throws Exception {
        //Given
        userInfoRepository.save(initialUser);
        userInfoRepository.save(initialUser2);
        userInfoRepository.save(initialAdmin);
        String jwt=jwtService.generateToken(initialAdmin.getEmail());

        initialAlbum2.setStatus("OFFLINE");
        repository.save(initialAlbum);
        repository.save(initialAlbum2);
        repository.save(initialAlbum3);

        //When
        mockMvc.perform(get("/album")
                .header("Authorization","Bearer "+jwt))

        //Then
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Dixee")))
                .andExpect(content().string(containsString("Ozie")))
                .andExpect(content().string(containsString("Freud")));
    }

    @Test
    @DisplayName("When User wants to get all albums, he get ONLINE ones")
    @WithMockUser(roles="USER")
    void testGetAllAlbumsAsuser() throws Exception {
        //Given
        userInfoRepository.save(initialUser);
        userInfoRepository.save(initialUser2);
        userInfoRepository.save(initialAdmin);
        String jwt=jwtService.generateToken(initialUser.getEmail());

        initialAlbum2.setStatus("OFFLINE");
        repository.save(initialAlbum);
        repository.save(initialAlbum2);
        repository.save(initialAlbum3);

        //When
        mockMvc.perform(get("/album")
                        .header("Authorization","Bearer "+jwt))

                //Then
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Dixee")))
                .andExpect(content().string(Matchers.not(containsString("Ozie"))))
                .andExpect(content().string(containsString("Freud")));
    }

    @Test
    @DisplayName("When owner wants to update an album, it is OK")
    @WithMockUser(roles="USER")
    void testUpdateAlbumByOwner() throws Exception {
        //Given
        userInfoRepository.save(initialUser);
        String jwt=jwtService.generateToken(initialUser.getEmail());
        Long id=repository.save(initialAlbum).getId();

        //When
        mockMvc.perform(put("/album/"+id)
                .header("Authorization","Bearer "+jwt)
                .param("name", "New Name")
                .param("description", "Newest Description"))

                //Then
                .andExpect(status().isOk());
        assertThat(repository.findById(id).get().getName()).isEqualTo("New Name");
    }

    @Test
    @DisplayName("When owner wants to update an album with wrong data, it is bad request")
    @WithMockUser(roles="USER")
    void testUpdateAlbumByOwnerWithWrongData() throws Exception {
        //Given
        userInfoRepository.save(initialUser);
        String jwt=jwtService.generateToken(initialUser.getEmail());
        Long id=repository.save(initialAlbum).getId();

        //When
        mockMvc.perform(put("/album/"+id)
                        .header("Authorization","Bearer "+jwt)
                        .param("name", "New Name"))

                //Then
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("When admin wants to update another's album, it is OK")
    @WithMockUser(roles="ADMIN")
    void testUpdateAlbumByAdmin() throws Exception {
        //Given
        userInfoRepository.save(initialUser);
        userInfoRepository.save(initialAdmin);
        String jwt=jwtService.generateToken(initialAdmin.getEmail());
        Long id=repository.save(initialAlbum).getId();

        //When
        mockMvc.perform(put("/album/"+id)
                        .header("Authorization","Bearer "+jwt)
                        .param("name", "New Name")
                        .param("description", "Newest Description"))

                //Then
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("When another user wants to update an album, it is bad request")
    @WithMockUser(roles="USER")
    void testUpdateAlbumByAnother() throws Exception {
        //Given
        userInfoRepository.save(initialUser);
        userInfoRepository.save(initialUser2);
        String jwt=jwtService.generateToken(initialUser2.getEmail());
        Long id=repository.save(initialAlbum).getId();

        //When
        mockMvc.perform(put("/album/"+id)
                        .header("Authorization","Bearer "+jwt)
                        .param("name", "New Name")
                        .param("description", "Newest Description"))

                //Then
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("When owner wants to delete their album, it works")
    @WithMockUser(roles="USER")
    void testDeleteAlbum() throws Exception {
        //Given
        userInfoRepository.save(initialUser);
        userInfoRepository.save(initialUser2);
        String jwt=jwtService.generateToken(initialUser.getEmail());
        Long id=repository.save(initialAlbum).getId();
        repository.save(initialAlbum2);
        assertThat(repository.findAll().size()).isEqualTo(2);

        //When
        mockMvc.perform(delete("/album/"+id)
                        .header("Authorization","Bearer "+jwt))

                //Then
                .andExpect(status().isOk());
        assertThat(repository.findAll().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("When owner wants to add a moderator, it works")
    @WithMockUser(roles="USER")
    void testAddModerator() throws Exception {
        //Given
        Long userId=userInfoRepository.save(initialUser).getId();
        userInfoRepository.save(initialUser2);
        String jwt=jwtService.generateToken(initialUser.getEmail());
        Long albumId=repository.save(initialAlbum).getId();



        //When
        mockMvc.perform(post("/user/"+userId+"/album/"+albumId+"/moderation")
                .header("Authorization","Bearer "+jwt)
                .param("moderatorEmail","test17@test.com"))

                //Then
                .andExpect(status().isOk());
        Album album=repository.findById(albumId).get();
        Hibernate.initialize(album);
        assertThat(album.getModerators().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("When owner wants to add himself as a moderator, it is bad request")
    @WithMockUser(roles="USER")
    void testAddMyselfAsModerator() throws Exception {
        //Given
        Long userId=userInfoRepository.save(initialUser).getId();
        userInfoRepository.save(initialUser2);
        String jwt=jwtService.generateToken(initialUser.getEmail());
        Long albumId=repository.save(initialAlbum).getId();

        //When
        mockMvc.perform(post("/user/"+userId+"/album/"+albumId+"/moderation")
                        .header("Authorization","Bearer "+jwt)
                        .param("moderatorEmail","test@test.com"))

                //Then
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("When user wants to add someone as a moderator, it is bad request")
    @WithMockUser(roles="USER")
    void testAddModeratorFromUser() throws Exception {
        //Given
        Long userId=userInfoRepository.save(initialUser).getId();
        userInfoRepository.save(initialUser2);
        String jwt=jwtService.generateToken(initialUser2.getEmail());
        Long albumId=repository.save(initialAlbum).getId();

        //When
        mockMvc.perform(post("/user/"+userId+"/album/"+albumId+"/moderation")
                        .header("Authorization","Bearer "+jwt)
                        .param("moderatorEmail","test@test.com"))

                //Then
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("When owner wants to remove a moderator, it works")
    @WithMockUser(roles="USER")
    void testRemoveModerator() throws Exception {
        //Given
        Long userId=userInfoRepository.save(initialUser).getId();
        Long moderatorId=userInfoRepository.save(initialUser2).getId();
        String jwt=jwtService.generateToken(initialUser.getEmail());
        initialAlbum.setModerators(List.of(initialUser2));
        Long albumId=repository.save(initialAlbum).getId();

        //When
        mockMvc.perform(delete("/user/"+userId+"/album/"+albumId+"/moderation/"+moderatorId)
                        .header("Authorization","Bearer "+jwt))

                //Then
                .andExpect(status().isOk());
        Album album=repository.findById(albumId).get();
        Hibernate.initialize(album);
        assertThat(album.getModerators().size()).isEqualTo(0);

    }

    @Test
    @DisplayName("When owner wants to remove inexistant moderator, it is bad request")
    @WithMockUser(roles="USER")
    void testRemoveInexistantModerator() throws Exception {
        //Given
        Long userId=userInfoRepository.save(initialUser).getId();
        Long moderatorId=userInfoRepository.save(initialUser2).getId();
        String jwt=jwtService.generateToken(initialUser.getEmail());
        Long albumId=repository.save(initialAlbum).getId();

        //When
        mockMvc.perform(delete("/user/"+userId+"/album/"+albumId+"/moderation/"+moderatorId)
                        .header("Authorization","Bearer "+jwt))

                //Then
                .andExpect(status().isBadRequest());

    }

    @Test
    @DisplayName("When moderator wants to remove themselves, it works")
    @WithMockUser(roles="USER")
    void testRemoveSelfAsModerator() throws Exception {
        //Given
        Long userId=userInfoRepository.save(initialUser).getId();
        Long moderatorId=userInfoRepository.save(initialUser2).getId();
        String jwt=jwtService.generateToken(initialUser2.getEmail());
        initialAlbum.setModerators(List.of(initialUser2));
        Long albumId=repository.save(initialAlbum).getId();

        //When
        mockMvc.perform(delete("/user/"+userId+"/album/"+albumId+"/moderation/"+moderatorId)
                        .header("Authorization","Bearer "+jwt))

                //Then
                .andExpect(status().isOk());
        Album album=repository.findById(albumId).get();
        Hibernate.initialize(album);
        assertThat(album.getModerators().size()).isEqualTo(0);

    }

    @Test
    @DisplayName("When moderator wants to remove another moderator, it is bad request")
    @WithMockUser(roles="USER")
    void testRemoveOtherModeratorAsModerator() throws Exception {
        //Given
        Long userId=userInfoRepository.save(initialUser).getId();
        userInfoRepository.save(initialUser2);
        Long moderatorId=userInfoRepository.save(initialAdmin).getId();
        String jwt=jwtService.generateToken(initialUser2.getEmail());
        initialAlbum.setModerators(List.of(initialUser2,initialAdmin));
        Long albumId=repository.save(initialAlbum).getId();

        //When
        mockMvc.perform(delete("/user/"+userId+"/album/"+albumId+"/moderation/"+moderatorId)
                        .header("Authorization","Bearer "+jwt))

                //Then
                .andExpect(status().isBadRequest());
        Album album=repository.findById(albumId).get();
        Hibernate.initialize(album);
        assertThat(album.getModerators().size()).isEqualTo(2);
    }
}
