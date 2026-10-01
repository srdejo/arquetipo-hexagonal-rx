package co.com.srdejo.domain.spi;

import co.com.srdejo.domain.model.ObjectModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IObjectPersistencePort {
    Mono<ObjectModel> saveObject(ObjectModel objectModel);

    Flux<ObjectModel> getAllObjects();
}
