package fr.ishtamar.starter.controllers;

import fr.ishtamar.starter.model.picvid.PicvidRepository;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.annotation.Secured;
import org.springframework.util.ResourceUtils;
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

    public AdminController(PicvidRepository picvidRepository){
        this.picvidRepository=picvidRepository;
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
}
