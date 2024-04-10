package fr.ishtamar.starter.controllers;

import fr.ishtamar.starter.filetransfer.FileUploadService;
import fr.ishtamar.starter.model.album.AlbumRepository;
import fr.ishtamar.starter.model.picvid.PicvidRepository;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.annotation.Secured;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

/**
 * AdminController is just there to be able to:
 *   - perform some CRON jobs
 *   - perform some manuel jobs in the database
 *       (especially to update it with new features)
 */
@RestController
@Slf4j
public class AdminController {
    @Value("${fr.ishtamar.starter.files-upload}")
    private String filesUpload;

    private final PicvidRepository picvidRepository;
    private final AlbumRepository albumRepository;
    private final FileUploadService fileUploadService;

    public AdminController(
            PicvidRepository picvidRepository,
            AlbumRepository albumRepository,
            FileUploadService fileUploadService
    ){
        this.picvidRepository=picvidRepository;
        this.albumRepository=albumRepository;
        this.fileUploadService=fileUploadService;
    }

    @Operation(hidden=true)
    @PutMapping("/admin/actions/filesizes")
    @Secured("ROLE_ADMIN")
    public void calculateNullFileSizes() {
        picvidRepository.findAll().stream()
                .filter(picvid->picvid.getFileSize()==null)
                .forEach(picvid-> {
                    try {
                        File file = ResourceUtils.getFile(filesUpload + "/" + picvid.getFileLocation());
                        picvid.setFileSize(file.length());
                        picvidRepository.save(picvid);
                    } catch(Exception e) {
                        log.warn("File " + picvid.getFileLocation() + " couldn't get size calculated");
                    }
                });
        log.info("FileSize calculation has been performed");
    }

    @Operation(hidden=true)
    @DeleteMapping("/admin/actions/cleanfs")
    @Secured("ROLE_ADMIN")
    public void deleteAllOrphanFiles() throws Exception {
        File folder=ResourceUtils.getFile(filesUpload);
        File[] files = folder.listFiles();

        for (File file : files) {
            if (file.isFile()) {
                if (file.getName().startsWith("crop-")) {
                    //We need to check if the non crop exists
                    File realFile= new File(filesUpload + "/" + file.getName().substring(5));
                    if (!realFile.exists()) {
                        fileUploadService.deletePicvidFromFS(file.getName().substring(5));
                        log.info("Orphan file {} has been auto-deleted", file.getName());
                    }
                } else {
                    //We need to check if name is registered in repo (album or picvid)
                    if (albumRepository.findAllByHomePicture(file.getName()).isEmpty()
                                    && picvidRepository.findAllByFileLocation(file.getName()).isEmpty()) {
                        fileUploadService.deletePicvidFromFS(file.getName());
                        log.info("Unused file {} has been auto-deleted",file.getName());
                        log.info("Unused file crop-{} has been auto-deleted",file.getName());
                    }
                }
            }
        }
        log.info("file system has been cleaned");
    }
}
