package co.com.srdejo.domain.usecase;

import co.com.srdejo.domain.api.IObjectServicePort;
import co.com.srdejo.domain.model.ObjectModel;
import co.com.srdejo.domain.spi.IObjectPersistencePort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ObjectUseCase implements IObjectServicePort {

    private final IObjectPersistencePort objectPersistencePort;

    public ObjectUseCase(IObjectPersistencePort objectPersistencePort) {
        this.objectPersistencePort = objectPersistencePort;
    }

    @Override
    public Mono<ObjectModel> saveObject(ObjectModel objectModel) {
        return objectPersistencePort.saveObject(objectModel);
    }

    @Override
    public Flux<ObjectModel> getAllObjects() {
        return objectPersistencePort.getAllObjects();
    }
}
