package pl.bartlomiejstepien.armaserverwebgui.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mission.model.MissionEntity;

import java.util.List;

@Repository
public interface MissionRepository extends JpaRepository<MissionEntity, Long>
{
    List<MissionEntity> findByTemplate(String template);

    @Modifying
    void deleteFirstByTemplate(String template);

    @Modifying
    @Query(nativeQuery = true, value = "UPDATE mission SET mission.enabled = true WHERE mission.template IN (:templates)")
    void updateAllByTemplateSetEnabled(@Param("templates") List<String> templates);

    @Modifying
    @Query(nativeQuery = true, value = "UPDATE mission SET mission.enabled = false")
    void disableAll();
}
