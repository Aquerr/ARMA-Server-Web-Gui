package pl.bartlomiejstepien.armaserverwebgui.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.ModPresetEntity;

import java.util.Optional;

@Repository
public interface ModPresetRepository extends JpaRepository<ModPresetEntity, Long>
{
    Optional<ModPresetEntity> findByName(String name);
}
