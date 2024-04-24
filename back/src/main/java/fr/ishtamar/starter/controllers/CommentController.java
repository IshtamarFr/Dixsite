package fr.ishtamar.starter.controllers;

import fr.ishtamar.starter.exceptionhandler.BadCredentialsException;
import fr.ishtamar.starter.exceptionhandler.EntityNotFoundException;
import fr.ishtamar.starter.exceptionhandler.GenericException;
import fr.ishtamar.starter.model.comment.*;
import fr.ishtamar.starter.model.picvid.Picvid;
import fr.ishtamar.starter.model.picvid.PicvidService;
import fr.ishtamar.starter.model.picvid.PicvidServiceImpl;
import fr.ishtamar.starter.model.user.UserInfo;
import fr.ishtamar.starter.model.user.UserInfoService;
import fr.ishtamar.starter.model.user.UserInfoServiceImpl;
import fr.ishtamar.starter.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@Slf4j
public class CommentController {
    private final UserInfoService userInfoService;
    private final JwtService jwtService;
    private final CommentService commentService;
    private final PicvidService picvidService;
    private final CommentMapper commentMapper;

    public CommentController(
            UserInfoServiceImpl userInfoService,
            JwtService jwtService,
            CommentServiceImpl commentService,
            PicvidServiceImpl picvidService,
            CommentMapper commentMapper
    ){
        this.userInfoService = userInfoService;
        this.jwtService = jwtService;
        this.commentService = commentService;
        this.picvidService=picvidService;
        this.commentMapper=commentMapper;
    }

    @Operation(summary = "gets all comments for a picture/video",responses={
            @ApiResponse(responseCode="200", description = "List of comments is served"),
            @ApiResponse(responseCode="403", description = "Access unauthorized"),
            @ApiResponse(responseCode="404", description = "Picture/video is not found")
    })
    @GetMapping("/album/{albumId}/picvid/{picvidId}/comment")
    @Secured("ROLE_USER")
    public List<CommentDto> getAllCommentsForPicvid(@PathVariable final Long picvidId) throws EntityNotFoundException {
        Picvid picvid=this.picvidService.getPicvidById(picvidId);
        return commentMapper.toDto(commentService.getAllCommentsByPicvid(picvid));
    }

    @Operation(summary = "creates a new comment or answer for a picture/video",responses={
            @ApiResponse(responseCode="200", description = "Comment or answer created"),
            @ApiResponse(responseCode="400", description = "Invalid request"),
            @ApiResponse(responseCode="403", description = "Access unauthorized"),
            @ApiResponse(responseCode="404", description = "User and/or picture/video is not found")
    })
    @PostMapping("/album/{albumId}/picvid/{picvidId}/comment")
    @Secured("ROLE_USER")
    public String createCommentForPicVid(
            @PathVariable final Long picvidId,
            @PathVariable final Long albumId,
            @RequestHeader(value="Authorization",required=false) String jwt,
            @RequestBody @Valid CreateCommentRequest request
            ) throws EntityNotFoundException, BadCredentialsException, GenericException {
        UserInfo sender=userInfoService.getUserByUsername(jwtService.extractUsername(jwt.substring(7)));
        Picvid picvid=picvidService.getPicvidById(picvidId);

        if (Objects.equals(picvid.getAlbum().getId(), albumId)) {
            commentService.createComment(request,sender,picvid,"ONLINE");
            return "Comment has been saved successfully";
        } else {
            throw new GenericException("Invalid request");
        }
    }

    @PutMapping("/album/{albumId}/picvid/{picvidId}/comment/{commentId}")
    @Secured({"ROLE_USER","ROLE_ADMIN"})
    public CommentDto moderatePicvid(
            @PathVariable final Long picvidId,
            @PathVariable final Long albumId,
            @PathVariable final Long commentId,
            @RequestHeader(value="Authorization",required=false) String jwt,
            @RequestParam String action
    ) throws EntityNotFoundException, BadCredentialsException, GenericException {
        UserInfo sender=userInfoService.getUserByUsername(jwtService.extractUsername(jwt.substring(7)));
        Picvid picvid=picvidService.getPicvidById(picvidId);
        Comment comment=commentService.getCommentById(commentId);

        if (sender.getRoles().contains("ROLE_ADMIN")) {
            return commentMapper.toDto(commentService.moderateComment(comment,action));
        } else if (Objects.equals(picvid.getAlbum().getId(), albumId)
                && Objects.equals(comment.getPicvid().getId(), picvidId)
                && (Objects.equals(sender, picvid.getAlbum().getOwner())
                    || picvid.getAlbum().getModerators().contains(sender))
        ) {
            return commentMapper.toDto(commentService.moderateComment(comment,action));
        } else {
            throw new GenericException("Invalid request");
        }
    }

    @Operation(summary = "gets all comments about self (comments, answers, owned albums)",responses={
            @ApiResponse(responseCode="200", description = "All comments are served"),
            @ApiResponse(responseCode="400", description = "User is illegitimate"),
            @ApiResponse(responseCode="403", description = "Access unauthorized"),
            @ApiResponse(responseCode="404", description = "User is not found")
    })
    @GetMapping("/user/{id}/comments")
    @Secured({"ROLE_USER","ROLE_ADMIN"})
    public Map<String,List<CommentDto>> getAllCommentsAboutUserById(
            @PathVariable final Long id,
            @RequestHeader(value="Authorization",required=false) String jwt
    ) throws EntityNotFoundException, GenericException {
        UserInfo sender=userInfoService.getUserByUsername(jwtService.extractUsername(jwt.substring(7)));
        UserInfo user=userInfoService.getUserById(id);

        if (Objects.equals(sender.getId(), id) || sender.getRoles().contains("ROLE_ADMIN")) {
            Map<String, List<CommentDto>> map = new HashMap<>();
            map.put("myComments",commentMapper.toDto(commentService.getAllCommentsByOwner(user)));
            map.put("myAlbumsComments",commentMapper.toDto(
                    commentService.getAllCommentsByAlbums(user.getOwnedAlbums())));
            return map;
        } else {
            throw new GenericException("You are not allowed to see this user's comments");
        }
    }
}
