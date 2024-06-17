package fr.ishtamar.dixsite.model.comment;

import fr.ishtamar.starter.exceptionhandler.EntityNotFoundException;
import fr.ishtamar.starter.exceptionhandler.GenericException;
import fr.ishtamar.dixsite.model.album.Album;
import fr.ishtamar.dixsite.model.picvid.Picvid;
import fr.ishtamar.starter.model.user.UserInfo;

import java.util.List;

public interface CommentService {
    List<Comment> getAllCommentsByPicvid(Picvid picvid);
    List<Comment> getAllCommentsByOwner(UserInfo user);
    List<Comment> getAllCommentsByAlbums(List<Album> albums);
    Comment getCommentById(final Long id) throws EntityNotFoundException;
    void createComment(
            CreateCommentRequest request,
            UserInfo owner,
            Picvid picvid,
            String status
    ) throws EntityNotFoundException, GenericException;
    Comment moderateComment(Comment comment,String action) throws GenericException;
}
