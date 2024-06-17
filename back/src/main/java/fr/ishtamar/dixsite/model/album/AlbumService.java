package fr.ishtamar.dixsite.model.album;

import fr.ishtamar.starter.exceptionhandler.EntityNotFoundException;
import fr.ishtamar.starter.exceptionhandler.GenericException;
import fr.ishtamar.starter.model.user.UserInfo;

import java.util.List;

public interface AlbumService {
    Album getAlbumById(final Long id) throws EntityNotFoundException;
    void createNewAlbum(UserInfo owner, CreateAlbumRequest request) throws Exception;
    Album modifyAlbum(Album album, ModifyAlbumRequest request) throws Exception;
    void subscribeToAlbum(final Long userId, final Long albumId) throws GenericException,EntityNotFoundException;
    void unSubscribeToAlbum(final Long userId, final Long albumId) throws GenericException,EntityNotFoundException;
    void changeStatus(Album album,String action,boolean isAdmin) throws GenericException;
    List<Album> findAll();
    List<Album> findByStatus(String status);
    void deleteAlbum(Album album);
    Album addModeratorToAlbum(Album album,String moderatorEmail) throws GenericException, EntityNotFoundException;
    Album removeModeratorFromAlbum(Album album,UserInfo moderator) throws GenericException;
}
