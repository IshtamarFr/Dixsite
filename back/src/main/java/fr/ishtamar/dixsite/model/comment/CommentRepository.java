package fr.ishtamar.dixsite.model.comment;

import fr.ishtamar.dixsite.model.picvid.Picvid;
import fr.ishtamar.starter.model.user.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByPicvidOrderByCreatedAtDesc(Picvid picvid);
    List<Comment> findAllByOwnerOrderByCreatedAtDesc(UserInfo user);
}
