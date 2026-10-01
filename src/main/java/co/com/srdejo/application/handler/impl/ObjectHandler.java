package co.com.srdejo.application.handler.impl;

import co.com.srdejo.application.dto.request.ObjectRequestDto;
import co.com.srdejo.application.dto.response.ObjectResponseDto;
import co.com.srdejo.application.handler.IObjectHandler;
import co.com.srdejo.application.mapper.IObjectRequestMapper;
import co.com.srdejo.application.mapper.IObjectResponseMapper;
import co.com.srdejo.domain.api.IObjectServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ObjectHandler implements IObjectHandler {

    private final IObjectServicePort objectServicePort;
    private final IObjectRequestMapper objectRequestMapper;
    private final IObjectResponseMapper objectResponseMapper;

    @Override
    public Mono<Void> saveObject(ObjectRequestDto objectRequestDto) {
        return objectServicePort.saveObject(objectRequestMapper.toObject(objectRequestDto))
                .then();
    }

    @Override
    public Flux<ObjectResponseDto> getAllObjects() {
        return objectServicePort.getAllObjects()
                .map(objectResponseMapper::toResponse);
    }
}
