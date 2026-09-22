package pl.bartlomiejstepien.armaserverwebgui.domain.editor;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.bartlomiejstepien.armaserverwebgui.domain.editor.exception.CouldNotSaveConfigFileContent;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.ServerConfigStorage;

@Service
@RequiredArgsConstructor
public class FileEditorService
{
    private final ServerConfigStorage serverConfigStorage;

    public void saveServerConfigContent(String content)
    {
        try
        {
            this.serverConfigStorage.saveServerConfigFileContent(content);
        }
        catch (Exception exception)
        {
            throw new CouldNotSaveConfigFileContent();
        }
    }

    public void saveBasicNetworkCongigContent(String content)
    {
        try
        {
            this.serverConfigStorage.saveBasicNetworkConfigFileContent(content);
        }
        catch (Exception exception)
        {
            throw new CouldNotSaveConfigFileContent();
        }
    }

    public String getBasicNetworkFileContent()
    {
        return this.serverConfigStorage.getRawNetworkConfigFileContent();
    }

    public String getServerConfigContent()
    {
        return this.serverConfigStorage.getRawServerConfigFileContent();
    }
}
