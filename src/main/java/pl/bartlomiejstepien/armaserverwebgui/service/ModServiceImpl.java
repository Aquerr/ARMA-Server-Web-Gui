package pl.bartlomiejstepien.armaserverwebgui.service;

import lombok.AllArgsConstructor;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import pl.bartlomiejstepien.armaserverwebgui.config.ASWGConfig;
import pl.bartlomiejstepien.armaserverwebgui.exception.ModFileAlreadyExistsException;
import pl.bartlomiejstepien.armaserverwebgui.model.Mods;
import pl.bartlomiejstepien.armaserverwebgui.storage.ModStorage;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;
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
        List<String> enabledMods = this.aswgConfig.getMods();
        Mods mods = new Mods();
        mods.setEnabledMods(enabledMods);
        mods.setDisabledMods(installedMods.stream()
                .filter(mod -> !enabledMods.contains(mod))
                .collect(Collectors.toList()));

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
        return this.modStorage.deleteMod(modName);
    }

    @Override
    public void saveEnabledModList(List<String> mods)
    {
        aswgConfig.setActiveMods(mods);
    }
}
