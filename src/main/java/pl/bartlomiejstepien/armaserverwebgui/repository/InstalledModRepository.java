package pl.bartlomiejstepien.armaserverwebgui.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.InstalledModEntity;

@Repository
public interface InstalledModRepository extends JpaRepository<InstalledModEntity, Long>
{
    Optional<InstalledModEntity> findByName(String name);

    Optional<InstalledModEntity> findByWorkshopFileId(Long id);

    List<InstalledModEntity> findAllByOrderByNameAsc();

    @Modifying
    @Query(nativeQuery = true, value = "UPDATE installed_mod SET installed_mod.enabled = false")
    void disableAllMods();

    @Modifying
    @Query(nativeQuery = true, value = "UPDATE installed_mod SET installed_mod.enabled = true WHERE installed_mod.workshop_file_id IN (:workshopFileIds)")
    void enableMods(@Param("workshopFileIds") List<Long> workshopFileIds);

    @Modifying
    @Query(nativeQuery = true, value = "UPDATE installed_mod SET installed_mod.server_mod = true WHERE installed_mod.workshop_file_id IN (:workshopFileIds)")
    void setServerMods(@Param("workshopFileIds") List<Long> workshopFileIds);
}
