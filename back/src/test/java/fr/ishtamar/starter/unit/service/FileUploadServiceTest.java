package fr.ishtamar.starter.unit.service;

import fr.ishtamar.starter.filetransfer.FileUploadService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class FileUploadServiceTest {
    @Autowired
    private FileUploadService fileUploadService;

    @Value("${fr.ishtamar.starter.files-upload}")
    private String filesUpload;

    File folder;

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

    @AfterEach
    @BeforeEach
    void clean(){
        try{
            folder= ResourceUtils.getFile(filesUpload);
            FileUtils.cleanDirectory(folder);
        } catch(Exception e){
            System.out.println(e);
        }
    }

    @Test
    @DisplayName("When I upload a file, it is stored in folder")
    void testUploadFile() throws Exception{
        //Given
        File file=new File("src/test/resources/dixee.jpg");
        FileInputStream input = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile("fileItem",
                file.getName(), "image/png", IOUtils.toByteArray(input));

        //When
        fileUploadService.saveFile(multipartFile);

        //Then
        assertThat(countFiles()).isEqualTo(2);
    }

    @Test
    //@Disabled
    @DisplayName("When I delete a file, it is deleted from folder")
    void testDeleteFromFSWorks() throws Exception {
        //Given
        folder= ResourceUtils.getFile(filesUpload);
        FileUtils.touch(new File(folder+"/abcdef.jpg"));
        FileUtils.touch(new File(folder+"/crop-abcdef.jpg"));
        FileUtils.touch(new File(folder+"/ghijkl.jpg"));
        FileUtils.touch(new File(folder+"/crop-ghijkljpg"));
        assertThat(this.countFiles()).isEqualTo(4);

        //When
        fileUploadService.deletePicvidFromFS("abcdef.jpg");

        //Then
        assertThat(this.countFiles()).isEqualTo(2);
    }
}
