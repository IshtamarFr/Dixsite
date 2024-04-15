package fr.ishtamar.starter.model.picvid;

import fr.ishtamar.starter.exceptionhandler.GenericException;
import fr.ishtamar.starter.model.album.Album;
import fr.ishtamar.starter.exceptionhandler.EntityNotFoundException;
import fr.ishtamar.starter.filetransfer.FileUploadResponse;
import fr.ishtamar.starter.filetransfer.FileUploadService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class PicvidServiceImpl implements PicvidService {
    @Value("${fr.ishtamar.starter.files-upload}")
    private String filesUpload;

    private final PicvidRepository repository;
    private final FileUploadService fileUploadService;

    public PicvidServiceImpl(PicvidRepository repository, FileUploadService fileUploadService) {
        this.repository = repository;
        this.fileUploadService=fileUploadService;
    }

    @Override
    public Picvid getPicvidById(Long id) throws EntityNotFoundException {
        return repository.findById(id).orElseThrow(
                ()->new EntityNotFoundException(Picvid.class,"id",id.toString()));
    }

    @Override
    public void createNewPicvid(Album album, CreatePicvidRequest request) throws Exception {
        if (album.getTotalSize()+request.getPicvid().getSize()<=album.getQuota()) {
            LocalDateTime now = LocalDateTime.now();
            FileUploadResponse fileUploadResponse = fileUploadService.saveFile(request.getPicvid());

            Picvid picvid = Picvid.builder()
                    .fileLocation(fileUploadResponse.getFileCodeExt())
                    .album(album)
                    .fileSize(request.getPicvid().getSize())
                    .createdAt(now)
                    .modifiedAt(now)
                    .build();

            if (fileUploadResponse.getFileCodeExt() != null)
                picvid.setDateTimeExif(fileUploadResponse.getDateTimeExif());
            if (request.getName() != null) picvid.setName(request.getName());
            if (request.getDescription() != null) picvid.setDescription(request.getDescription());
            if (request.getDate() != null) picvid.setDateTime(request.getDate());
            if (request.getTakenLocation() != null) picvid.setTakenLocation(request.getTakenLocation());

            repository.save(picvid);
        } else {
            throw new GenericException("Quota exceeded");
        }
    }

    @Override
    public List<Picvid> getAllPicvidsByAlbum(Album album) {
        return repository.findAllByAlbum(album);
    }

    @Override
    public String deletePicvidById(Long id) throws EntityNotFoundException {
        //We need to delete fileCode and crop-fileCode as well
        String deletedFiles="List of deleted files: ";
        String fileCode=getPicvidById(id).getFileLocation();

        if (FileUtils.deleteQuietly(FileUtils.getFile(filesUpload + "/" + fileCode)))
            deletedFiles+=filesUpload + "/" + fileCode + " ";

        if (FileUtils.deleteQuietly(FileUtils.getFile(filesUpload + "/crop-" + fileCode)))
            deletedFiles+=filesUpload + "/crop-" + fileCode + " ";

        repository.deleteById(id);
        return deletedFiles;
    }

    @Override
    public void createNewPicvids(Album album, List<MultipartFile> picvids) throws Exception {
        Long totalSize=picvids.stream().mapToLong(MultipartFile::getSize).sum();
        if (album.getTotalSize()+totalSize<=album.getQuota()) {
            LocalDateTime now = LocalDateTime.now();

            picvids.forEach(x->{
                try {
                    FileUploadResponse fileUploadResponse = fileUploadService.saveFile(x);

                    Picvid picvid = Picvid.builder()
                            .fileLocation(fileUploadResponse.getFileCodeExt())
                            .album(album)
                            .fileSize(x.getSize())
                            .createdAt(now)
                            .modifiedAt(now)
                            .build();

                    if (fileUploadResponse.getFileCodeExt() != null)
                        picvid.setDateTimeExif(fileUploadResponse.getDateTimeExif());

                    repository.save(picvid);

                } catch (Exception e) {
                    log.warn("Couldn't save file {}",x.getName());
                }
            });
        } else {
            throw new GenericException("Quota exceeded");
        }
    }
}
