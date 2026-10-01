package co.com.srdejo.application.mapper;

import co.com.srdejo.application.dto.response.ObjectResponseDto;
import co.com.srdejo.domain.model.ObjectModel;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IObjectResponseMapper {
    ObjectResponseDto toResponse(ObjectModel objectModel);
}
