package fr.ishtamar.dixsite.model.album;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {
    List<Album> findByStatus(String status);
    List<Album> findAllByHomePicture(String string);
}
