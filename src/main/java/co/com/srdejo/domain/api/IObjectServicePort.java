package co.com.srdejo.domain.api;

import co.com.srdejo.domain.model.ObjectModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IObjectServicePort {

    Mono<ObjectModel> saveObject(ObjectModel objectModel);

    Flux<ObjectModel> getAllObjects();
}
