package co.com.srdejo.application.handler;

import co.com.srdejo.application.dto.request.ObjectRequestDto;
import co.com.srdejo.application.dto.response.ObjectResponseDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IObjectHandler {

    Mono<Void> saveObject(ObjectRequestDto objectRequestDto);

    Flux<ObjectResponseDto> getAllObjects();
}
