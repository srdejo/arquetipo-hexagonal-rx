package co.com.srdejo.infrastructure.out.r2dbc.adapter;

import co.com.srdejo.domain.model.ObjectModel;
import co.com.srdejo.domain.spi.IObjectPersistencePort;
import co.com.srdejo.infrastructure.exception.NoDataFoundException;
import co.com.srdejo.infrastructure.out.r2dbc.mapper.IObjectEntityMapper;
import co.com.srdejo.infrastructure.out.r2dbc.repository.IObjectRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ObjectR2dbcAdapter implements IObjectPersistencePort {

    private final IObjectRepository objectRepository;
    private final IObjectEntityMapper objectEntityMapper;

    @Override
    public Mono<ObjectModel> saveObject(ObjectModel objectModel) {
        return objectRepository.save(objectEntityMapper.toEntity(objectModel))
                .map(objectEntityMapper::toObjectModel);
    }

    @Override
    public Flux<ObjectModel> getAllObjects() {
        return objectRepository.findAll()
                .switchIfEmpty(Flux.error(new NoDataFoundException()))
                .map(objectEntityMapper::toObjectModel);
    }
}
