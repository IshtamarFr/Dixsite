package fr.ishtamar.starter.model.picvid;

import fr.ishtamar.starter.model.album.Album;
import fr.ishtamar.starter.exceptionhandler.EntityNotFoundException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface PicvidService {
    Picvid getPicvidById(final Long id) throws EntityNotFoundException;
    void createNewPicvid(Album album, CreatePicvidRequest request) throws Exception;
    void createNewPicvids(Album album, List<MultipartFile> picvids) throws Exception;
    List<Picvid> getAllPicvidsByAlbum(Album album);
    String deletePicvidById(final Long id) throws EntityNotFoundException;
}
