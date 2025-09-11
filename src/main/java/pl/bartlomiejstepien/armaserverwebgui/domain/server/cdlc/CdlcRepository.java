package pl.bartlomiejstepien.armaserverwebgui.domain.server.cdlc;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.cdlc.model.CdlcEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface CdlcRepository extends JpaRepository<CdlcEntity, Long>
{
    Optional<CdlcEntity> findByName(String name);

    List<CdlcEntity> findAllByEnabledTrue();
}
