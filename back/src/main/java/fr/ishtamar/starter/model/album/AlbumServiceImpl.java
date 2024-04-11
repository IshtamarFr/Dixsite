package fr.ishtamar.starter.model.album;

import fr.ishtamar.starter.exceptionhandler.EntityNotFoundException;
import fr.ishtamar.starter.exceptionhandler.GenericException;
import fr.ishtamar.starter.filetransfer.FileUploadService;
import fr.ishtamar.starter.model.user.UserInfo;
import fr.ishtamar.starter.model.user.UserInfoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
public class AlbumServiceImpl implements AlbumService {
    private final AlbumRepository repository;
    private final FileUploadService fileUploadService;
    private final UserInfoService userService;

    @Value("${fr.ishtamar.starter.quota}")
    private Long QUOTA;

    public AlbumServiceImpl(
            AlbumRepository repository,
            FileUploadService fileUploadService,
            UserInfoService userService)
    {
        this.repository = repository;
        this.fileUploadService = fileUploadService;
        this.userService=userService;
    }

    @Override
    public Album getAlbumById(final Long id) throws EntityNotFoundException {
        return repository.findById(id)
                .orElseThrow(()->new EntityNotFoundException(Album.class,"id",id.toString()));
    }

    @Override
    public void createNewAlbum(UserInfo owner, CreateAlbumRequest request) throws Exception {
        if (owner.getOwnedAlbums().size()<owner.getMaxAlbums()) {
            LocalDateTime now = LocalDateTime.now();
            Album album = Album.builder()
                    .owner(owner)
                    .name(request.getName())
                    .description(request.getDescription())
                    .status("ONLINE")
                    .createdAt(now)
                    .modifiedAt(now)
                    .quota(QUOTA * 1048576)
                    .build();

            if (request.getHomePicture() != null)
                album.setHomePicture(fileUploadService.saveFile(request.getHomePicture()).getFileCodeExt());
            repository.save(album);
        } else {
            throw new GenericException("Exceeded album quota");
        }
    }

    @Override
    public void subscribeToAlbum(Long userId, Long albumId) throws GenericException,EntityNotFoundException {
        Album album=this.getAlbumById(albumId);
        UserInfo user=userService.getUserById(userId);

        if (Objects.equals(user, album.getOwner())){
            throw new GenericException("It is not allowed to subscribe to own album");
        }else if (album.getSubscribers().contains(user)){
            throw new GenericException("User has already subscribed to this album");
        }else {
            album.getSubscribers().add(user);
            repository.save(album);
        }
    }

    @Override
    public void unSubscribeToAlbum(Long userId, Long albumId) throws GenericException, EntityNotFoundException {
        Album album=this.getAlbumById(albumId);
        UserInfo user=userService.getUserById(userId);

        if (!album.getSubscribers().contains(user)){
            throw new GenericException("User had not subscribed to this album");
        }else {
            album.getSubscribers().removeAll(Collections.singletonList(user));
            repository.save(album);
        }
    }

    @Override
    public void changeStatus(Album album, String action, boolean isAdmin) throws GenericException {
        String[] actions = {"ONLINE", "OFFLINE", "MODERATED"};

        if (Arrays.asList(actions).contains(action)) {
            if (isAdmin) {
                album.setStatus(action);
                repository.save(album);
            } else if (!Objects.equals(album.getStatus(), "MODERATED")) {
                album.setStatus(action);
                repository.save(album);
            } else {
                throw new GenericException("User is not allowed to change this status");
            }
        }else{
            throw new GenericException("This action VERB is not allowed");
        }
    }

    @Override
    public List<Album> findAll() {
        return repository.findAll();
    }

    @Override
    public List<Album> findByStatus(String status) {
        return repository.findByStatus(status);
    }

    @Override
    public Album modifyAlbum(Album album, ModifyAlbumRequest request) throws Exception {
        album.setModifiedAt(LocalDateTime.now());
        album.setName(request.getName());
        album.setDescription(request.getDescription());

        if (album.getHomePicture()==null) {
            //No home picture, we save it if it exists
            if (request.getHomePicture()!=null)
                album.setHomePicture(fileUploadService.saveFile(request.getHomePicture()).getFileCodeExt());
        } else {
            if (request.getHomePicture()!=null) {
                //A picture exists and a new one is served : we need to delete old one and save new one
                fileUploadService.deletePicvidFromFS(album.getHomePicture());
                album.setHomePicture(fileUploadService.saveFile(request.getHomePicture()).getFileCodeExt());
            } else {
                //A picture exists but no one is served : we need to delete it if its EMPTY. No change otherwise
                if (Objects.equals(request.getIsHomePictureEmpty(), "true")) {
                    fileUploadService.deletePicvidFromFS(album.getHomePicture());
                    album.setHomePicture(null);
                }
            }
        }
        return repository.save(album);
    }

    @Override
    public void deleteAlbum(Album album) {
        if (album.getHomePicture()!=null) fileUploadService.deletePicvidFromFS(album.getHomePicture());
        repository.delete(album);
    }

    @Override
    public void addModeratorToAlbum(Album album, String moderatorEmail) throws GenericException, EntityNotFoundException {
        UserInfo moderator=userService.getUserByUsername(moderatorEmail);

        if (!Objects.equals(album.getOwner(),moderator)) {
            if (album.getModerators().contains(moderator)) {
                throw new GenericException("This moderator is already registered");
            } else {
                album.getModerators().add(moderator);
            }
        } else {
            throw new GenericException("You cannot add yourself as a moderator");
        }
    }
}
