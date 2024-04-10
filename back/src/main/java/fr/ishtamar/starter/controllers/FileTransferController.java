package fr.ishtamar.starter.controllers;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/file")
@Slf4j
public class FileTransferController {
    @Value("${fr.ishtamar.starter.files-upload}")
    private String filesUpload;

    //TODO: OpenAPI
    @ResponseBody
    @GetMapping(value="/{fileCode}", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getJpegImage(@PathVariable("fileCode") final String fileCode) throws IOException {
        try {
            File file=ResourceUtils.getFile(filesUpload + "/" + fileCode);
            return FileUtils.readFileToByteArray(file);
        } catch (Exception e) {
            log.warn(e.toString());
            File file=ResourceUtils.getFile("src/main/resources/crop-default.jpg");
            return FileUtils.readFileToByteArray(file);
        }
    }
}
