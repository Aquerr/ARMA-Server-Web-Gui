package pl.bartlomiejstepien.armaserverwebgui.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.bartlomiejstepien.armaserverwebgui.storage.ModStorage;

import java.util.List;

@Service
@AllArgsConstructor
public class ModServiceImpl implements ModService
{
    private final ModStorage modStorage;

    @Override
    public List<String> getInstalledModNames()
    {
        return this.modStorage.getInstalledModNames();
    }
}
