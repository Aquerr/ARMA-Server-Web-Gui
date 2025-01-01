package pl.bartlomiejstepien.armaserverwebgui.interfaces.user.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import pl.bartlomiejstepien.armaserverwebgui.interfaces.user.model.AswgUserEntity;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends ReactiveCrudRepository<AswgUserEntity, Integer>
{
    Mono<AswgUserEntity> findByUsername(String username);
}
