package co.com.srdejo.infrastructure.configuration;

import co.com.srdejo.domain.api.IObjectServicePort;
import co.com.srdejo.domain.spi.IObjectPersistencePort;
import co.com.srdejo.domain.usecase.ObjectUseCase;
import co.com.srdejo.infrastructure.out.r2dbc.adapter.ObjectR2dbcAdapter;
import co.com.srdejo.infrastructure.out.r2dbc.mapper.IObjectEntityMapper;
import co.com.srdejo.infrastructure.out.r2dbc.repository.IObjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class BeanConfiguration {
    private final IObjectRepository objectRepository;
    private final IObjectEntityMapper objectEntityMapper;

    @Bean
    public IObjectPersistencePort objectPersistencePort() {
        return new ObjectR2dbcAdapter(objectRepository, objectEntityMapper);
    }

    @Bean
    public IObjectServicePort objectServicePort() {
        return new ObjectUseCase(objectPersistencePort());
    }
}
