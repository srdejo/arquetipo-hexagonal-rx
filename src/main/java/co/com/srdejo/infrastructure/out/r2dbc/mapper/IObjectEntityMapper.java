package co.com.srdejo.infrastructure.out.r2dbc.mapper;

import co.com.srdejo.domain.model.ObjectModel;
import co.com.srdejo.infrastructure.out.r2dbc.entity.ObjectEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE
)
public interface IObjectEntityMapper {

    ObjectEntity toEntity(ObjectModel objectModel);

    ObjectModel toObjectModel(ObjectEntity objectEntity);
}
