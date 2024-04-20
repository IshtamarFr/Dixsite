package fr.ishtamar.starter.filetransfer;

import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {
    FileUploadResponse saveFile(MultipartFile multipartFile) throws Exception;
    void deletePicvidFromFS(String fileCode);
}
