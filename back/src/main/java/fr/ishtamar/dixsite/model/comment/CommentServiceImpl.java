package fr.ishtamar.dixsite.model.comment;

import fr.ishtamar.starter.exceptionhandler.EntityNotFoundException;
import fr.ishtamar.starter.exceptionhandler.GenericException;
import fr.ishtamar.dixsite.model.album.Album;
import fr.ishtamar.dixsite.model.picvid.Picvid;
import fr.ishtamar.starter.model.user.UserInfo;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService{
    private final CommentRepository repository;

    public CommentServiceImpl(CommentRepository commentRepository) {
        this.repository=commentRepository;
    }

    @Override
    public List<Comment> getAllCommentsByPicvid(Picvid picvid) {
        return repository.findAllByPicvidOrderByCreatedAtDesc(picvid).stream()
                .filter(x->x.getMother()==null)
                .collect(Collectors.toList());
    }

    @Override
    public Comment getCommentById(Long id) throws EntityNotFoundException {
        return repository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException(Comment.class,"id",id.toString()));
    }

    @Override
    public void createComment(
            CreateCommentRequest request,
            UserInfo owner,Picvid picvid,
            String status
    ) throws EntityNotFoundException,GenericException {
        Comment comment= Comment.builder()
                .owner(owner)
                .createdAt(LocalDateTime.now())
                .content(request.getContent())
                .status(status)
                .picvid(picvid)
                .build();

        //We need to check if message is answer to an already answer
        if (request.getMother_id()!=null ) {
            Comment mother=this.getCommentById(request.getMother_id());
            if (mother.getMother()!=null) {
                throw new GenericException("You cant answer to an already answer");
            } else {
                comment.setMother(mother);
                repository.save(comment);
            }
        } else {
            repository.save(comment);
        }
    }

    @Override
    public List<Comment> getAllCommentsByOwner(UserInfo user) {
        return repository.findAllByOwnerOrderByCreatedAtDesc(user);
    }

    @Override
    public List<Comment> getAllCommentsByAlbums(List<Album> albums) {
        return repository.findAll().stream()
                .filter(comment->albums.contains(comment.getPicvid().getAlbum()))
                .sorted(Comparator.comparing(Comment::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public Comment moderateComment(Comment comment, String action) throws GenericException {
        switch (action) {
            case "moderate" -> {
                if (comment.getStatus().equals("ONLINE")) {
                    comment.setStatus("MODERATED");
                    return repository.save(comment);
                } else {
                    throw new GenericException("Comment is already moderated");
                }
            }
            case "unmoderate" -> {
                if (comment.getStatus().equals("MODERATED")) {
                    comment.setStatus("ONLINE");
                    return repository.save(comment);
                } else {
                    throw new GenericException("Comment is already online or is moderated by Admin");
                }
            }
            case "supermoderate" -> {
                comment.setStatus("ADMIN");
                return repository.save(comment);
            }
            case "unsupermoderate" -> {
                comment.setStatus("ONLINE");
                return repository.save(comment);
            }
            default -> throw new GenericException("This action is not registered as valid action");
        }
    }
}
