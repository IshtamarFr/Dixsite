package fr.ishtamar.starter.integration.controller;

import fr.ishtamar.starter.model.album.Album;
import fr.ishtamar.starter.model.album.AlbumRepository;
import fr.ishtamar.starter.model.picvid.Picvid;
import fr.ishtamar.starter.model.picvid.PicvidRepository;
import fr.ishtamar.starter.model.user.UserInfo;
import fr.ishtamar.starter.model.user.UserInfoRepository;
import fr.ishtamar.starter.security.JwtService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDateTime;

import static fr.ishtamar.starter.security.SecurityConfig.passwordEncoder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PicvidControllerIT {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    PicvidRepository repository;
    @Autowired
    AlbumRepository albumRepository;
    @Autowired
    UserInfoRepository userInfoRepository;
    @Autowired
    JwtService jwtService;

    @Value("${fr.ishtamar.starter.files-upload}")
    private String filesUpload;

    File folder;

    final UserInfo initialUser=UserInfo.builder()
            .name("Ishta")
            .email("test@test.com")
            .password(passwordEncoder().encode("123456"))
            .roles("ROLE_USER")
            .build();

    final UserInfo initialUser2=UserInfo.builder()
            .name("Pal")
            .email("test17@test.com")
            .password(passwordEncoder().encode("654321"))
            .roles("ROLE_USER")
            .build();

    final Album initialAlbum=Album.builder()
            .name("Dixee")
            .owner(initialUser)
            .quota(200000000L)
            .description("La plus belle")
            .status("ONLINE")
            .createdAt(LocalDateTime.now())
            .modifiedAt(LocalDateTime.now())
            .build();

    final Album initialAlbum2=Album.builder()
            .name("Ozie")
            .owner(initialUser)
            .quota(200000000L)
            .description("On ne l'aime pas")
            .status("ONLINE")
            .createdAt(LocalDateTime.now())
            .modifiedAt(LocalDateTime.now())
            .build();

    final Picvid initialPicvid=Picvid.builder()
            .fileLocation("ABCD1234.jpg")
            .createdAt(LocalDateTime.now())
            .modifiedAt(LocalDateTime.now())
            .album(initialAlbum)
            .fileSize(1234567L)
            .build();

    final Picvid initialPicvid2=Picvid.builder()
            .fileLocation("AAAA7777.jpg")
            .createdAt(LocalDateTime.now())
            .modifiedAt(LocalDateTime.now())
            .album(initialAlbum)
            .fileSize(555555L)
            .build();

    @BeforeEach
    @AfterEach
    void clean() {
        repository.deleteAll();
        albumRepository.deleteAll();
        userInfoRepository.deleteAll();
        try{
            folder= ResourceUtils.getFile(filesUpload);
            FileUtils.cleanDirectory(folder);
        } catch(Exception e){
            System.out.println(e);
        }
    }

    @Test
    @WithMockUser(roles="USER")
    @DisplayName("When I try to upload a correct picvid to my album, it works")
    void testCreateNewPicvid() throws Exception {
        //Given
        userInfoRepository.save(initialUser);
        String jwt= jwtService.generateToken(initialUser.getEmail());
        Long id=albumRepository.save(initialAlbum).getId();
        repository.save(initialPicvid);

        File file=new File("src/test/resources/dixee.jpg");
        FileInputStream input = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile("fileItem",
                file.getName(), "image/png", IOUtils.toByteArray(input));

        //When
        mockMvc.perform(multipart("/album/"+id+"/picvid")
                .file("picvid",multipartFile.getBytes())
                .param("name","Dixee chérie")
                .param("description","Dixee a gagné le concours de beauté !")
                .header("Authorization","Bearer "+jwt))

        //Then
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles="USER")
    @DisplayName("When I try to upload a correct picvid to another's album, an error is thrown")
    void testCreateNewPicvidOnAnotherAlbum() throws Exception {
        //Given
        userInfoRepository.save(initialUser);
        userInfoRepository.save(initialUser2);
        String jwt= jwtService.generateToken(initialUser2.getEmail());
        Long id=albumRepository.save(initialAlbum).getId();
        repository.save(initialPicvid);

        File file=new File("src/test/resources/dixee.jpg");
        FileInputStream input = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile("fileItem",
                file.getName(), "image/png", IOUtils.toByteArray(input));

        //When
        mockMvc.perform(multipart("/album/"+id+"/picvid")
                        .file("picvid",multipartFile.getBytes())
                        .param("name","Dixee chérie")
                        .param("description","Dixee a gagné le concours de beauté !")
                        .header("Authorization","Bearer "+jwt))

                //Then
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles="USER")
    //@Disabled
    @DisplayName("When I try to upload correct multiple picvids to my album, it works")
    void testCreateNewMultiplePicvids() throws Exception {
        //Given
        userInfoRepository.save(initialUser);
        String jwt= jwtService.generateToken(initialUser.getEmail());
        Long id=albumRepository.save(initialAlbum).getId();
        repository.save(initialPicvid);

        File file=new File("src/test/resources/dixee.jpg");
        FileInputStream input = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile("fileItem",
                file.getName(), "image/png", IOUtils.toByteArray(input));

        //When
        mockMvc.perform(multipart("/album/"+id+"/picvids")
                        .file("picvid", multipartFile.getBytes())
                        .file("picvid", multipartFile.getBytes())
                        .header("Authorization","Bearer "+jwt))

                //Then
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles="USER")
    @DisplayName("When I try to get all picvids for an album, it works")
    void testGetAllPicvidsByAlbum() throws Exception {
        //Given
        userInfoRepository.save(initialUser);
        String jwt= jwtService.generateToken(initialUser.getEmail());
        Long id=albumRepository.save(initialAlbum).getId();
        repository.save(initialPicvid);

        //When
        mockMvc.perform(get("/album/"+id+"/picvid")
                        .header("Authorization","Bearer "+jwt))

        //Then
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles="USER")
    @DisplayName("When I try to get all picvids for a moderated album, it is a bad request")
    void testGetAllPicvidsForModeratedAlbum() throws Exception {
        //Given
        userInfoRepository.save(initialUser);
        String jwt= jwtService.generateToken(initialUser.getEmail());
        initialAlbum.setStatus("MODERATED");
        Long id=albumRepository.save(initialAlbum).getId();
        repository.save(initialPicvid);

        //When
        mockMvc.perform(get("/album/"+id+"/picvid")
                        .header("Authorization","Bearer "+jwt))

                //Then
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles="USER")
    @DisplayName("When admin tries to get all picvids for a moderated album, it is OK")
    void testAdminGetAllPicvidsForModeratedAlbum() throws Exception {
        //Given
        initialUser.setRoles("ROLE_USER,ROLE_ADMIN");
        userInfoRepository.save(initialUser);
        String jwt= jwtService.generateToken(initialUser.getEmail());
        initialAlbum.setStatus("MODERATED");
        Long id=albumRepository.save(initialAlbum).getId();
        repository.save(initialPicvid);

        //When
        mockMvc.perform(get("/album/"+id+"/picvid")
                        .header("Authorization","Bearer "+jwt))

                //Then
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles="USER")
    @DisplayName("When I try to get a valid picvid, it works")
    void testGetPicvidByIdByAlbum() throws Exception {
        //Given
        userInfoRepository.save(initialUser);
        String jwt= jwtService.generateToken(initialUser.getEmail());
        Long id=albumRepository.save(initialAlbum).getId();
        Long picvidId=repository.save(initialPicvid).getId();

        //When
        mockMvc.perform(get("/album/"+id+"/picvid/"+picvidId)
                        .header("Authorization","Bearer "+jwt))

                //Then
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles="USER")
    @DisplayName("When I try to get a picvid with invalid album, an error is thrown")
    void testGetPicvidByIdForWrongAlbum() throws Exception {
        //Given
        userInfoRepository.save(initialUser);
        String jwt= jwtService.generateToken(initialUser.getEmail());
        albumRepository.save(initialAlbum);
        Long id=albumRepository.save(initialAlbum2).getId();
        Long picvidId=repository.save(initialPicvid).getId();

        //When
        mockMvc.perform(get("/album/"+id+"/picvid/"+picvidId)
                        .header("Authorization","Bearer "+jwt))

                //Then
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles="USER")
    @DisplayName("When I try to get a valid picvid for a moderated album, it is bad request")
    void testGetPicvidByIdForModeratedAlbum() throws Exception {
        //Given
        userInfoRepository.save(initialUser);
        String jwt= jwtService.generateToken(initialUser.getEmail());
        initialAlbum.setStatus("MODERATED");
        Long id=albumRepository.save(initialAlbum).getId();
        Long picvidId=repository.save(initialPicvid).getId();

        //When
        mockMvc.perform(get("/album/"+id+"/picvid/"+picvidId)
                        .header("Authorization","Bearer "+jwt))

                //Then
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles="USER")
    @DisplayName("When I try to delete a valid picvid , all is OK")
    void testDeletePicvidByIdByAlbum() throws Exception {
        //Given
        userInfoRepository.save(initialUser);
        String jwt= jwtService.generateToken(initialUser.getEmail());
        Long id=albumRepository.save(initialAlbum).getId();
        Long picvidId=repository.save(initialPicvid).getId();

        //When
        mockMvc.perform(delete("/album/"+id+"/picvid/"+picvidId)
                        .header("Authorization","Bearer "+jwt))

                //Then
                .andExpect(status().isOk());
    }
}
