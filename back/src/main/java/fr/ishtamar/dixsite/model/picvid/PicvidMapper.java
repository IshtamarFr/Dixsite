package fr.ishtamar.dixsite.model.picvid;

import fr.ishtamar.starter.util.EntityMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "Spring")
public interface PicvidMapper extends EntityMapper<PicvidDto, Picvid> {
    @Mappings({
            @Mapping(source= "picvid.album.id",target="album_id")
    })
    PicvidDto toDto(Picvid picvid);
}
