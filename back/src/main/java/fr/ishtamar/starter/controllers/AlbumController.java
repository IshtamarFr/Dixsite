package fr.ishtamar.starter.controllers;

import fr.ishtamar.starter.exceptionhandler.BadCredentialsException;
import fr.ishtamar.starter.exceptionhandler.EntityNotFoundException;
import fr.ishtamar.starter.exceptionhandler.GenericException;
import fr.ishtamar.starter.model.album.*;
import fr.ishtamar.starter.security.JwtService;
import fr.ishtamar.starter.model.user.UserInfo;
import fr.ishtamar.starter.model.user.UserInfoService;
import fr.ishtamar.starter.model.user.UserInfoServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
public class AlbumController {
    private final AlbumService albumService;
    private final JwtService jwtService;
    private final UserInfoService userInfoService;
    private final AlbumMapper albumMapper;

    public AlbumController(AlbumServiceImpl albumService, UserInfoServiceImpl userInfoService, AlbumMapper albumMapper, JwtService jwtService) {
        this.albumService = albumService;
        this.userInfoService = userInfoService;
        this.albumMapper = albumMapper;
        this.jwtService=jwtService;
    }

    @Operation(summary = "gets list of all albums for given user",responses={
            @ApiResponse(responseCode="200", description = "Personal data is displayed"),
            @ApiResponse(responseCode="403", description = "Access unauthorized"),
            @ApiResponse(responseCode="404", description = "User not found")
    })
    @GetMapping("/user/{userId}/album")
    @Secured("ROLE_USER")
    public Map<String,List<AlbumDto>> listAllAlbums(@PathVariable final Long userId) throws EntityNotFoundException {
        Map<String,List<AlbumDto>>map=new HashMap<>();
        UserInfo owner=userInfoService.getUserById(userId);

        map.put("owned",albumMapper.toDto(owner.getOwnedAlbums()));
        map.put("moderated",albumMapper.toDto(owner.getModeratedAlbums()));
        map.put("subscribed",albumMapper.toDto(
                owner.getSubscribedAlbums().stream()
                        .filter(x->x.getStatus().equals("ONLINE"))
                        .collect(Collectors.toList())
        ));
        return(map);
    }

    @Operation(summary = "creates a new Album for user",responses={
            @ApiResponse(responseCode="200", description = "Album is created"),
            @ApiResponse(responseCode="400", description = "User is illegitimate"),
            @ApiResponse(responseCode="403", description = "Access unauthorized")
    })
    @PostMapping(value="/user/{userId}/album",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Secured("ROLE_USER")
    public String CreateNewAlbum(@PathVariable final Long userId,
                               @RequestHeader(value="Authorization",required=false) String jwt,
                               @Valid CreateAlbumRequest request)
    throws Exception {
        UserInfo sender=userInfoService.getUserByUsername(jwtService.extractUsername(jwt.substring(7)));
        if (!Objects.equals(userId, sender.getId())){
            throw new BadCredentialsException();
        }else{
            albumService.createNewAlbum(sender,request);
            return "Album successfully created";
        }
    }

    @Operation(summary = "user subscribes to album",responses={
            @ApiResponse(responseCode="200", description = "Album subscribed"),
            @ApiResponse(responseCode="400", description = "User is illegitimate or album is already subscribed"),
            @ApiResponse(responseCode="403", description = "Access unauthorized"),
            @ApiResponse(responseCode="404", description = "User or album is not found")
    })
    @PostMapping("/user/{userId}/album/{albumId}/subscribe")
    @Secured("ROLE_USER")
    public String subscribeAlbum(
            @PathVariable final Long userId,
            @PathVariable final Long albumId,
            @RequestHeader(value="Authorization",required=false) String jwt)
            throws BadCredentialsException, GenericException,EntityNotFoundException {
        UserInfo sender=userInfoService.getUserByUsername(jwtService.extractUsername(jwt.substring(7)));
        if (!Objects.equals(userId, sender.getId())){
            throw new BadCredentialsException();
        }else{
            albumService.subscribeToAlbum(userId,albumId);
            return "User " + userId + " successfully subscribed to album " + albumId;
        }
    }

    @Operation(summary = "user unsubscribes to album",responses={
            @ApiResponse(responseCode="200", description = "Album unsubscribed"),
            @ApiResponse(responseCode="400", description = "User is illegitimate or album hadn't been subscribed"),
            @ApiResponse(responseCode="403", description = "Access unauthorized"),
            @ApiResponse(responseCode="404", description = "User or album is not found")
    })
    @DeleteMapping("/user/{userId}/album/{albumId}/subscribe")
    @Secured("ROLE_USER")
    public String unSubscribeAlbum(
            @PathVariable final Long userId,
            @PathVariable final Long albumId,
            @RequestHeader(value="Authorization",required=false) String jwt)
            throws BadCredentialsException, GenericException,EntityNotFoundException {
        UserInfo sender=userInfoService.getUserByUsername(jwtService.extractUsername(jwt.substring(7)));
        if (!Objects.equals(userId, sender.getId())){
            throw new BadCredentialsException();
        }else{
            albumService.unSubscribeToAlbum(userId,albumId);
            return "User " + userId + " successfully unsubscribed to album " + albumId;
        }
    }

    @Operation(summary = "owner/admin changes album status",responses={
            @ApiResponse(responseCode="200", description = "Album status changed"),
            @ApiResponse(responseCode="400", description = "User is illegitimate or album status can't bne modified"),
            @ApiResponse(responseCode="403", description = "Access unauthorized"),
            @ApiResponse(responseCode="404", description = "User or album is not found")
    })
    @PutMapping("/user/{userId}/album/{albumId}/status")
    @Secured({"ROLE_USER","ROLE_ADMIN"})
    public String modifyAlbumStatus(
            @PathVariable final Long userId,
            @PathVariable final Long albumId,
            @RequestHeader(value="Authorization",required=false) String jwt,
            @RequestBody @Valid AlbumStatusChangeRequest request)
    throws BadCredentialsException,EntityNotFoundException,GenericException {
        UserInfo sender=userInfoService.getUserByUsername(jwtService.extractUsername(jwt.substring(7)));
        Album album=albumService.getAlbumById(albumId);

        if (sender.getRoles().contains("ADMIN")) {
            albumService.changeStatus(album, request.getAction(),true);
            return "User " + userId + " successfully modified by admin";
        } else if (!Objects.equals(userId, sender.getId())) {
            throw new BadCredentialsException();
        } else if (Objects.equals(userId,album.getOwner().getId())) {
            albumService.changeStatus(album, request.getAction(),false);
            return "User " + userId + " successfully modified by themselves";
        } else {
            return "Action has been cancelled";
        }
    }

    @Operation(summary = "user gets all existing albums",responses={
            @ApiResponse(responseCode="200", description = "ONLINE Albums are displayed"),
            @ApiResponse(responseCode="403", description = "Access unauthorized"),
            @ApiResponse(responseCode="404", description = "User not found")
    })
    @GetMapping("/album")
    @Secured({"ROLE_USER","ROLE_ADMIN"})
    public List<AlbumDto> getAllAlbums(@RequestHeader(value="Authorization",required=false) String jwt) throws EntityNotFoundException {
        UserInfo sender=userInfoService.getUserByUsername(jwtService.extractUsername(jwt.substring(7)));

        if (sender.getRoles().contains("ADMIN")) {
            return albumMapper.toDto(albumService.findAll());
        }else {
            return albumMapper.toDto(albumService.findByStatus("ONLINE"));
        }
    }

    @Operation(summary = "user gets a specific album",responses={
            @ApiResponse(responseCode="200", description = "ONLINE Albums are displayed"),
            @ApiResponse(responseCode="403", description = "Access unauthorized")
    })
    @GetMapping("/album/{id}")
    @Secured("ROLE_USER")
    public AlbumDto getAlbumById(@PathVariable final Long id) throws EntityNotFoundException {
        return albumMapper.toDto(albumService.getAlbumById(id));
    }

    //TODO: OpenAPI
    @PutMapping("/album/{id}")
    @Secured({"ROLE_USER","ROLE_ADMIN"})
    public AlbumDto modifyAlbumById(
            @PathVariable final Long id,
            @RequestHeader(value="Authorization",required=false) String jwt,
            @Valid ModifyAlbumRequest request
    ) throws Exception {
        UserInfo sender=userInfoService.getUserByUsername(jwtService.extractUsername(jwt.substring(7)));
        Album album=albumService.getAlbumById(id);

        if (sender.getRoles().contains("ADMIN") || Objects.equals(sender.getId(),album.getOwner().getId())) {
            return albumMapper.toDto(albumService.modifyAlbum(album,request));
        } else {
            throw new GenericException("You are not allowed to modify this album");
        }
    }

    //TODO: OpenAPI
    @DeleteMapping("/album/{id}")
    @Secured({"ROLE_USER","ROLE_ADMIN"})
    public String deleteAlbumById(
            @PathVariable final Long id,
            @RequestHeader(value="Authorization",required=false) String jwt
    ) throws EntityNotFoundException,GenericException {
        UserInfo sender=userInfoService.getUserByUsername(jwtService.extractUsername(jwt.substring(7)));
        Album album=albumService.getAlbumById(id);

        if (sender.getRoles().contains("ADMIN") || Objects.equals(sender.getId(),album.getOwner().getId())) {
            albumService.deleteAlbum(album);
            return "Album deleted with success";
        } else {
            throw new GenericException("You are not allowed to modify this album");
        }
    }

    //TODO: OpenAPI
    @PostMapping("/user/{userId}/album/{albumId}/moderation")
    @Secured("ROLE_USER")
    public String AddNewValidModerator(
            @PathVariable final Long userId,
            @PathVariable final Long albumId,
            @RequestParam final String moderatorEmail,
            @RequestHeader(value="Authorization",required=false) String jwt
    ) throws EntityNotFoundException,GenericException {
        UserInfo sender=userInfoService.getUserByUsername(jwtService.extractUsername(jwt.substring(7)));
        Album album=albumService.getAlbumById(albumId);

        if (sender.getRoles().contains("ADMIN")) {
            albumService.addModeratorToAlbum(album,moderatorEmail);
            return "User " + userId + " successfully added to moderators for album " + albumId;
        } else if (!Objects.equals(userId, sender.getId())) {
            throw new BadCredentialsException();
        } else if (Objects.equals(userId,album.getOwner().getId())) {
            albumService.addModeratorToAlbum(album,moderatorEmail);
            return "User " + userId + " successfully added to moderators for album " + albumId;
        } else {
            return "Action has been cancelled";
        }
    }
}