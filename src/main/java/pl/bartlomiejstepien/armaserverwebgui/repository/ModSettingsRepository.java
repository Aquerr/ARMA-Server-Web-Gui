package pl.bartlomiejstepien.armaserverwebgui.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.ModSettingsEntity;

public interface ModSettingsRepository extends JpaRepository<ModSettingsEntity, Long>
{
    Optional<ModSettingsEntity> findByName(String name);

    @Modifying
    @Query(nativeQuery = true, value = "UPDATE mod_settings SET mod_settings.active = FALSE")
    void disableAll();

    Optional<ModSettingsEntity> findFirstByActiveTrue();

    @Modifying
    @Query(nativeQuery = true, value = "UPDATE mod_settings SET mod_settings.active = FALSE WHERE id = :id")
    void deactivate(@Param("id") Long id);
}
