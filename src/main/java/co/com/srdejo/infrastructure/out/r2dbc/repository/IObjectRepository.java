package co.com.srdejo.infrastructure.out.r2dbc.repository;

import co.com.srdejo.infrastructure.out.r2dbc.entity.ObjectEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface IObjectRepository extends ReactiveCrudRepository<ObjectEntity, Long> {

}
