package pl.bartlomiejstepien.armaserverwebgui.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.bartlomiejstepien.armaserverwebgui.domain.user.model.AswgUserEntity;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<AswgUserEntity, Integer>
{
    Optional<AswgUserEntity> findByUsername(String username);
}
