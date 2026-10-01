package co.com.srdejo.domain;

import co.com.srdejo.domain.model.ObjectModel;
import co.com.srdejo.domain.spi.IObjectPersistencePort;
import co.com.srdejo.domain.usecase.ObjectUseCase;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ObjectUseCaseTest {

    private final IObjectPersistencePort port = mock(IObjectPersistencePort.class);
    private final ObjectUseCase useCase = new ObjectUseCase(port);

    @Test
    void saveObjectDelegatesToPort() {
        ObjectModel model = new ObjectModel(null, "test");
        ObjectModel saved = new ObjectModel(1L, "test");

        when(port.saveObject(model)).thenReturn(Mono.just(saved));

        StepVerifier.create(useCase.saveObject(model)).expectNext(saved).verifyComplete();
    }

    @Test
    void getAllObjectsEmitsEveryObjectFromPort() {
        ObjectModel first = new ObjectModel(1L, "first");
        ObjectModel second = new ObjectModel(2L, "second");

        when(port.getAllObjects()).thenReturn(Flux.just(first, second));

        StepVerifier.create(useCase.getAllObjects()).expectNext(first, second).verifyComplete();
    }
}
