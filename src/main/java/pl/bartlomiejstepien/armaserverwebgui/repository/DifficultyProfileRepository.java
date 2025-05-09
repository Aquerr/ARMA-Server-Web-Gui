package pl.bartlomiejstepien.armaserverwebgui.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.difficulty.model.DifficultyProfileEntity;

import java.util.Optional;

@Repository
public interface DifficultyProfileRepository extends JpaRepository<DifficultyProfileEntity, Integer>
{
    Optional<DifficultyProfileEntity> findFirstByActiveTrue();

    Optional<DifficultyProfileEntity> findByName(String name);
}
