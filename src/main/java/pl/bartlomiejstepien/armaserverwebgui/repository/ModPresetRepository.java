package pl.bartlomiejstepien.armaserverwebgui.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.ModPresetEntity;

@Repository
public interface ModPresetRepository extends JpaRepository<ModPresetEntity, Long>
{
    Optional<ModPresetEntity> findByName(String name);
}
