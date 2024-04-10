package fr.ishtamar.starter.unit.service;

import fr.ishtamar.starter.exceptionhandler.GenericException;
import fr.ishtamar.starter.model.album.Album;
import fr.ishtamar.starter.filetransfer.FileUploadResponse;
import fr.ishtamar.starter.filetransfer.FileUploadService;
import fr.ishtamar.starter.model.picvid.CreatePicvidRequest;
import fr.ishtamar.starter.model.picvid.Picvid;
import fr.ishtamar.starter.model.picvid.PicvidRepository;
import fr.ishtamar.starter.model.picvid.PicvidServiceImpl;
import fr.ishtamar.starter.model.user.UserInfo;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Optional;

import static fr.ishtamar.starter.security.SecurityConfig.passwordEncoder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
public class PicvidServiceImplTest {
    @Autowired
    PicvidServiceImpl picvidServiceImpl;
    @MockBean
    PicvidRepository picvidRepository;
    @MockBean
    FileUploadService fileUploadService;

    @Value("${fr.ishtamar.starter.files-upload}")
    private String filesUpload;

    File folder;

    final static UserInfo initialUser=UserInfo.builder()
            .id(1L)
            .name("Ishta")
            .email("test@test.com")
            .password(passwordEncoder().encode("123456"))
            .roles("ROLE_USER")
            .build();

    final static Album initialAlbum=Album.builder()
            .id(789L)
            .name("Dixee")
            .owner(initialUser)
            .description("La plus belle")
            .status("ONLINE")
            .createdAt(LocalDateTime.now())
            .modifiedAt(LocalDateTime.now())
            .quota(2L)
            .build();

    int countFiles() {
        File[] files = folder.listFiles();
        int count = 0;
        for (File file : files) {
            if (file.isFile()) {
                count++;
            }
        }
        return count;
    }

    @BeforeEach
    @AfterEach
    void clean(){
        try{
            folder= ResourceUtils.getFile(filesUpload);
            FileUtils.cleanDirectory(folder);
        } catch(Exception e){
            System.out.println(e);
        }
    }

    @Test
    @DisplayName("When I delete a picvid, it is deleted from database AND from folder (both cropped and normal)")
    void testDeletePicvid() throws Exception {
        //Given
        folder= ResourceUtils.getFile(filesUpload);
        FileUtils.touch(new File(folder+"/abcdef.jpg"));
        FileUtils.touch(new File(folder+"/crop-abcdef.jpg"));
        FileUtils.touch(new File(folder+"/ghijkl.jpg"));
        FileUtils.touch(new File(folder+"/crop-ghijkljpg"));
        assertThat(this.countFiles()).isEqualTo(4);

        LocalDateTime now=LocalDateTime.now();

        Picvid mockPickVid= Picvid.builder()
                .id(777L)
                .createdAt(now)
                .modifiedAt(now)
                .album(new Album())
                .fileLocation("abcdef.jpg")
                .build();

        when(picvidRepository.findById(777L)).thenReturn(Optional.of(mockPickVid));

        //When
        picvidServiceImpl.deletePicvidById(777L);

        //Then
        assertThat(this.countFiles()).isEqualTo(2);
        verify(picvidRepository,times(1)).findById(777L);
    }

    @Test
    @DisplayName("When I try to create a new picvid, it works")
    void testAddPicvidToAlbum() throws Exception {
        //Given
        LocalDateTime now=LocalDateTime.of(2024, 3, 10, 16, 51);
        try (MockedStatic<LocalDateTime> mock = Mockito.mockStatic(LocalDateTime.class)) {
            mock.when(LocalDateTime::now).thenReturn(now);

            MultipartFile picvid = new MockMultipartFile("fileItem",
                    "mockPicture.jpg", "image/jpeg", new byte[0]);

            CreatePicvidRequest request= CreatePicvidRequest.builder()
                    .name(picvid.getName())
                    .picvid(picvid)
                    .description("J'aime la galette")
                    .build();

            FileUploadResponse mockFileUploadResponse=FileUploadResponse.builder()
                    .fileCodeExt("A1B2C3D4")
                    .build();

            Picvid expectedPicvid=Picvid.builder()
                    .fileLocation("A1B2C3D4")
                    .fileSize(0L)
                    .name("fileItem")
                    .description("J'aime la galette")
                    .album(initialAlbum)
                    .createdAt(now)
                    .modifiedAt(now)
                    .build();

            when(fileUploadService.saveFile(picvid)).thenReturn(mockFileUploadResponse);
            when(LocalDateTime.now()).thenReturn(now);

            //When
            picvidServiceImpl.createNewPicvid(initialAlbum,request);

            //Then
            verify(picvidRepository,times(1)).save(expectedPicvid);
        }
    }

    @Test
    @DisplayName("When I try to create a new picvid, it fails if quota is exceeded")
    void testAddPicvidToAlbumWithQuota() throws Exception {
        //Given
        LocalDateTime now=LocalDateTime.of(2024, 3, 10, 16, 51);
        try (MockedStatic<LocalDateTime> mock = Mockito.mockStatic(LocalDateTime.class)) {
            mock.when(LocalDateTime::now).thenReturn(now);

            MultipartFile picvid = new MockMultipartFile("fileItem",
                    "mockPicture.jpg", "image/jpeg", new byte[100]);

            CreatePicvidRequest request= CreatePicvidRequest.builder()
                    .name(picvid.getName())
                    .picvid(picvid)
                    .description("J'aime la galette")
                    .build();

            FileUploadResponse mockFileUploadResponse=FileUploadResponse.builder()
                    .fileCodeExt("A1B2C3D4")
                    .build();

            Picvid expectedPicvid=Picvid.builder()
                    .fileLocation("A1B2C3D4")
                    .name("fileItem")
                    .description("J'aime la galette")
                    .album(initialAlbum)
                    .createdAt(now)
                    .modifiedAt(now)
                    .build();

            when(fileUploadService.saveFile(picvid)).thenReturn(mockFileUploadResponse);
            when(LocalDateTime.now()).thenReturn(now);

            //When
            assertThrows(GenericException.class,()->picvidServiceImpl.createNewPicvid(initialAlbum,request));

            //Then
            verify(picvidRepository,times(0)).save(expectedPicvid);
        }
    }
}
