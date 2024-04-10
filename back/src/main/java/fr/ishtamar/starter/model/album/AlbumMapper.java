package fr.ishtamar.starter.model.album;

import fr.ishtamar.starter.model.picvid.Picvid;
import fr.ishtamar.starter.util.EntityMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper(componentModel = "Spring")
public interface AlbumMapper extends EntityMapper<AlbumDto, Album> {
    @Mappings({
            @Mapping(source= "album.owner.id",target="owner_id"),
            @Mapping(source= "album.owner.name",target="owner_name"),
            @Mapping(source = "picvids", target = "picvid_ids", qualifiedByName = "picvidToId")
    })
    AlbumDto toDto(Album album);

    @Named("picvidToId")
    static Long picvidToId(Picvid picvid) {
        return picvid.getId();
    }
}
