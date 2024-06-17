package fr.ishtamar.dixsite.controllers;

import fr.ishtamar.dixsite.model.album.Album;
import fr.ishtamar.dixsite.model.album.AlbumService;
import fr.ishtamar.dixsite.model.album.AlbumServiceImpl;
import fr.ishtamar.dixsite.model.picvid.*;
import fr.ishtamar.starter.exceptionhandler.EntityNotFoundException;
import fr.ishtamar.starter.exceptionhandler.GenericException;
import fr.ishtamar.starter.security.JwtService;
import fr.ishtamar.starter.model.user.UserInfo;
import fr.ishtamar.starter.model.user.UserInfoService;
import fr.ishtamar.starter.model.user.UserInfoServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/album/{id}")
@Slf4j
public class PicvidController {
    private final UserInfoService userInfoService;
    private final AlbumService albumService;
    private final JwtService jwtService;
    private final PicvidService picvidService;
    private final PicvidMapper picvidMapper;

    public PicvidController(
            UserInfoServiceImpl userInfoService,
            AlbumServiceImpl albumService,
            JwtService jwtService,
            PicvidServiceImpl picvidService,
            PicvidMapper picvidMapper
    ){
        this.userInfoService = userInfoService;
        this.albumService = albumService;
        this.jwtService = jwtService;
        this.picvidService = picvidService;
        this.picvidMapper=picvidMapper;
    }

    @Operation(summary = "post a new picture/video for album",responses={
            @ApiResponse(responseCode="200", description = "Picture/video uploaded"),
            @ApiResponse(responseCode="400", description = "User is illegitimate or quota is exceeded"),
            @ApiResponse(responseCode="403", description = "Access unauthorized"),
            @ApiResponse(responseCode="404", description = "User and/or album is not found")
    })
    @PostMapping(value="/picvid",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Secured("ROLE_USER")
    public String addPicvid(
            @RequestHeader(value="Authorization",required=false) String jwt,
            @PathVariable final Long id,
            @Valid CreatePicvidRequest request
            ) throws Exception {
        UserInfo sender=userInfoService.getUserByUsername(jwtService.extractUsername(jwt.substring(7)));
        Album album=albumService.getAlbumById(id);

        if(Objects.equals(sender.getId(), album.getOwner().getId()) || album.getModerators().contains(sender)){
            picvidService.createNewPicvid(album, request);
            return "Picvid has been added with success";
        }else{
            throw new GenericException("Only the owner can add pictures or videos to album");
        }
    }

    @Operation(summary = "post multiple pictures/videos for album",responses={
            @ApiResponse(responseCode="200", description = "Pictures/videos uploaded"),
            @ApiResponse(responseCode="400", description = "User is illegitimate or quota is exceeded"),
            @ApiResponse(responseCode="403", description = "Access unauthorized"),
            @ApiResponse(responseCode="404", description = "User and/or album is not found")
    })
    @PostMapping(value="/picvids",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Secured("ROLE_USER")
    public String addPicvidInBatch(
            @RequestHeader(value="Authorization",required=false) String jwt,
            @PathVariable final Long id,
            @RequestPart(name="picvid") List<MultipartFile> pictures
    ) throws Exception {
        UserInfo sender=userInfoService.getUserByUsername(jwtService.extractUsername(jwt.substring(7)));
        Album album=albumService.getAlbumById(id);

        if(Objects.equals(sender.getId(), album.getOwner().getId()) || album.getModerators().contains(sender)){
            picvidService.createNewPicvids(album, pictures);
            return "Picvids have been added with success";
        }else{
            throw new GenericException("Only the owner can add pictures or videos to album");
        }
    }

    @Operation(summary = "Get all pictures/videos for album",responses={
            @ApiResponse(responseCode="200", description = "List of pictures/videos is served"),
            @ApiResponse(responseCode="400", description = "Album is not available"),
            @ApiResponse(responseCode="403", description = "Access unauthorized"),
            @ApiResponse(responseCode="404", description = "User and/or album is not found")
    })
    @GetMapping("/picvid")
    @Secured("ROLE_USER")
    public List<PicvidDto> GetAllPicvidsForAlbum(
            @PathVariable final Long id,
            @RequestHeader(value="Authorization",required=false) String jwt
    ) throws EntityNotFoundException,GenericException {
        UserInfo user=userInfoService.getUserByUsername(jwtService.extractUsername(jwt.substring(7)));
        Album album=albumService.getAlbumById(id);

        if (Objects.equals(album.getStatus(), "ONLINE") || user.getRoles().contains("ADMIN")) {
            return picvidMapper.toDto(picvidService.getAllPicvidsByAlbum(album));
        }else if (!Objects.equals(album.getStatus(), "MODERATED") && Objects.equals(album.getOwner(),user)) {
            return picvidMapper.toDto(picvidService.getAllPicvidsByAlbum(album));
        }else{
            throw new GenericException("This album is not disponible");
        }
    }

    @Operation(summary = "Get a specific picture/video",responses={
            @ApiResponse(responseCode="200", description = "Picture/video is served"),
            @ApiResponse(responseCode="400", description = "Album is not available"),
            @ApiResponse(responseCode="403", description = "Access unauthorized"),
            @ApiResponse(responseCode="404", description = "User and/or album is not found")
    })
    @GetMapping("/picvid/{picvidId}")
    @Secured("ROLE_USER")
    public PicvidDto getPicvidDetails(
            @PathVariable final Long picvidId,
            @PathVariable final Long id,
            @RequestHeader(value="Authorization",required=false) String jwt
    ) throws EntityNotFoundException,GenericException {
        UserInfo user=userInfoService.getUserByUsername(jwtService.extractUsername(jwt.substring(7)));
        Album album=albumService.getAlbumById(id);

        if (Objects.equals(album.getStatus(), "ONLINE") || user.getRoles().contains("ADMIN")) {
            Picvid picvid=picvidService.getPicvidById(picvidId);

            if (Objects.equals(picvid.getAlbum().getId(),id)){
                return picvidMapper.toDto(picvid);
            }else {
                throw new GenericException("This picture mismatches this album's id");
            }
        }else {
            throw new GenericException("This album is not disponible");
        }
    }

    @Operation(summary = "deletes a specific picture/video",responses={
            @ApiResponse(responseCode="200", description = "Picture/video is served"),
            @ApiResponse(responseCode="400", description = "Invalid request or resource unavailable"),
            @ApiResponse(responseCode="403", description = "Access unauthorized"),
            @ApiResponse(responseCode="404", description = "User and/or album and/or picture/video is not found")
    })
    @DeleteMapping("/picvid/{picvidId}")
    @Secured("ROLE_USER")
    public String deletePicvid(
            @PathVariable final Long picvidId,
            @PathVariable final Long id,
            @RequestHeader(value="Authorization",required=false) String jwt
    ) throws EntityNotFoundException,GenericException {
        UserInfo user=userInfoService.getUserByUsername(jwtService.extractUsername(jwt.substring(7)));
        Album album=albumService.getAlbumById(id);
        Picvid picvid=picvidService.getPicvidById(picvidId);

        if (Objects.equals(picvid.getAlbum().getId(),id)){
            if (Objects.equals(album.getOwner(),user)
                    || user.getRoles().contains("ADMIN")
                    || album.getModerators().contains(user)
            ) {
                picvidService.deletePicvidById(picvid.getId());
                return "Picvid " + picvidId + " successfully deleted";
            } else {
                throw new GenericException("You are not allowed to delete this resource");
            }
        }else {
            throw new GenericException("This picture mismatches this album's id");
        }
    }

    //TODO: OpenAPI
    @PutMapping("/picvid/{picvidId}")
    @Secured("ROLE_USER")
    public PicvidDto modifyPicvid(
            @RequestHeader(value="Authorization",required=false) String jwt,
            @PathVariable final Long id,
            @PathVariable final Long picvidId,
            @Valid ModifyPicvidRequest request
    ) throws Exception {
        UserInfo sender=userInfoService.getUserByUsername(jwtService.extractUsername(jwt.substring(7)));
        Album album=albumService.getAlbumById(id);
        Picvid picvid=picvidService.getPicvidById(picvidId);

        if (!Objects.equals(album,picvid.getAlbum())) {
            throw new GenericException("This picvid Id mismatches this album Id");
        } else if(Objects.equals(sender.getId(), album.getOwner().getId()) || album.getModerators().contains(sender)){
            return picvidMapper.toDto(picvidService.modifyPicvid(picvid, request));
        }else{
            throw new GenericException("Only the owner can modify pictures or videos to album");
        }
    }
}
