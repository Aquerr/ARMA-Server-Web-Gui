package pl.bartlomiejstepien.armaserverwebgui.domain.server.cdlc;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.cdlc.converter.CdlcConverter;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.cdlc.dto.Cdlc;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.cdlc.dto.CdlcDirectory;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.cdlc.model.CdlcEntity;

import java.util.List;

@Service
@AllArgsConstructor
public class CdlcService
{
    private final CdlcRepository cdlcRepository;
    private final CdlcFileStorageService cdlcFileStorageService;
    private final CdlcConverter cdlcConverter;

    @Transactional(readOnly = true)
    public List<Cdlc> findAll()
    {
        List<CdlcEntity> entities = cdlcRepository.findAll();
        List<CdlcDirectory> cdlcDirectories = cdlcFileStorageService.findInstalledCdlcs(entities.stream()
                .map(CdlcEntity::getDirectoryName)
                .toList());
        return cdlcConverter.convert(entities, cdlcDirectories);
    }

    @Transactional(readOnly = true)
    public List<Cdlc> findEnabledCdlc()
    {
        List<CdlcEntity> entities = cdlcRepository.findAllByEnabledTrue();
        List<CdlcDirectory> cdlcDirectories = cdlcFileStorageService.findInstalledCdlcs(entities.stream()
                .map(CdlcEntity::getDirectoryName)
                .toList());
        return cdlcConverter.convert(entities, cdlcDirectories);
    }

    @Transactional
    public void toggleCdlc(long id)
    {
        CdlcEntity cdlcEntity = cdlcRepository.findById(id).orElseThrow();
        cdlcEntity.setEnabled(!cdlcEntity.isEnabled());
        cdlcRepository.save(cdlcEntity);
    }
}
