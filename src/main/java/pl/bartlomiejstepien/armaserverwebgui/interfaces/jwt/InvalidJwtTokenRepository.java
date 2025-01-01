package pl.bartlomiejstepien.armaserverwebgui.interfaces.jwt;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import pl.bartlomiejstepien.armaserverwebgui.application.security.jwt.model.InvalidJwtTokenEntity;
import reactor.core.publisher.Mono;

@Repository
public interface InvalidJwtTokenRepository extends ReactiveCrudRepository<InvalidJwtTokenEntity, Integer>
{
    @Modifying
    @Query("DELETE FROM invalid_jwt_token WHERE invalid_jwt_token.expiration_date_time < NOW()")
    Mono<Void> deleteAllByExpirationDateTimeBeforeNow();
}
