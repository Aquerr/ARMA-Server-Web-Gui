package pl.bartlomiejstepien.armaserverwebgui.application.security.jwt;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.bartlomiejstepien.armaserverwebgui.application.security.jwt.model.InvalidJwtTokenEntity;

@Repository
public interface InvalidJwtTokenRepository extends JpaRepository<InvalidJwtTokenEntity, Integer>
{
    @Modifying
    @Query(nativeQuery = true, value = "DELETE FROM invalid_jwt_token WHERE invalid_jwt_token.expiration_date_time < NOW()")
    void deleteAllByExpirationDateTimeBeforeNow();
}
