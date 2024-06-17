package fr.ishtamar.dixsite.model.comment;

import fr.ishtamar.starter.util.EntityMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "Spring")
public interface CommentMapper extends EntityMapper<CommentDto,Comment> {
    @Mappings({
            @Mapping(source= "owner.id",target="owner_id"),
            @Mapping(source= "owner.name",target="owner_name"),
            @Mapping(source= "picvid.id",target="picvid_id"),
            @Mapping(source= "picvid.album.id",target="album_id"),
            @Mapping(source= "mother.id",target="mother_id"),
            @Mapping(source= "children", target = "subcomments", qualifiedByName = "commentToDto")
    })
    CommentDto toDto(Comment comment);

    @Named("commentToDto")
    static CommentDto commentToDto(Comment comment) {
        return CommentDto.builder()
                //They are children so they have mother
                .id(comment.getId())
                .content(comment.getContent())
                .status(comment.getStatus())
                .createdAt(comment.getCreatedAt())
                .owner_id(comment.getOwner().getId())
                .owner_name(comment.getOwner().getName())
                .picvid_id(comment.getPicvid().getId())
                .album_id(comment.getPicvid().getAlbum().getId())
                .mother_id(comment.getMother().getId())
                .build();
    }
}
