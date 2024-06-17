package fr.ishtamar.dixsite.model.picvid;

import fr.ishtamar.dixsite.model.album.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PicvidRepository extends JpaRepository<Picvid,Long> {
    List<Picvid> findAllByAlbum(Album album);
    List<Picvid> findAllByFileLocation(String string);
}
