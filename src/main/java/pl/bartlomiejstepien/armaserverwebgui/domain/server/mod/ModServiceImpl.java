package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod;

import lombok.AllArgsConstructor;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import pl.bartlomiejstepien.armaserverwebgui.application.config.ASWGConfig;
import pl.bartlomiejstepien.armaserverwebgui.domain.model.Mod;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.exception.ModFileAlreadyExistsException;
import pl.bartlomiejstepien.armaserverwebgui.domain.model.Mods;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.mod.ModStorage;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ModServiceImpl implements ModService
{
    private final ModStorage modStorage;
    private final ASWGConfig aswgConfig;

    @Override
    public Mono<Void> save(FilePart multipartFile)
    {
        if(modStorage.doesModExists(multipartFile.filename()))
            throw new ModFileAlreadyExistsException();

        try
        {
            return modStorage.save(multipartFile);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Mods getMods()
    {
        List<String> installedMods = getInstalledModNames();
        Set<Mod> enabledMods = this.aswgConfig.getActiveMods();
        Mods mods = new Mods();
        mods.setEnabledMods(enabledMods);
        mods.setDisabledMods(installedMods.stream()
                .filter(modName -> enabledMods.stream()
                        .noneMatch(mod -> mod.getName().equals(modName)))
                .map(modName -> new Mod(modName, false))
                .collect(Collectors.toSet()));
        return mods;
    }

    @Override
    public List<String> getInstalledModNames()
    {
        return this.modStorage.getInstalledModNames();
    }

    @Override
    public boolean deleteMod(String modName)
    {
        Set<Mod> enabledMods = this.aswgConfig.getActiveMods();
        enabledMods.removeIf(mod -> mod.getName().equals(modName));
        saveEnabledModList(enabledMods);
        return this.modStorage.deleteMod(modName);
    }

    @Override
    public void saveEnabledModList(Set<Mod> mods)
    {
        aswgConfig.setActiveMods(mods);
    }
}