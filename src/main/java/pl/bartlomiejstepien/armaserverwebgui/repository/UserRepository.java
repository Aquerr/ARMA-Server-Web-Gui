package pl.bartlomiejstepien.armaserverwebgui.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.bartlomiejstepien.armaserverwebgui.domain.user.model.AswgUserEntity;

import java.time.OffsetDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<AswgUserEntity, Integer>
{
    Optional<AswgUserEntity> findByUsername(String username);

    @Modifying
    @Query("UPDATE AswgUserEntity entity SET entity.lastSuccessLoginDateTime = :loginDateTime WHERE entity.id = :userId")
    void updateLastSuccessLoginDateTime(@Param("userId") int userId, @Param("loginDateTime") OffsetDateTime loginDateTime);
}
