package pl.bartlomiejstepien.armaserverwebgui.domain.editor;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.bartlomiejstepien.armaserverwebgui.application.file.exception.CouldNotParseFileException;
import pl.bartlomiejstepien.armaserverwebgui.application.file.exception.CouldNotSaveConfigFileContent;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.ServerConfigStorage;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.exception.ParsingException;

@Service
@RequiredArgsConstructor
public class FileEditorService
{
    private final ServerConfigStorage serverConfigStorage;

    public void saveServerConfigContent(String content)
    {
        try
        {
            this.serverConfigStorage.saveServerConfigFileContent(content, true);
        }
        catch (ParsingException e)
        {
            throw new CouldNotParseFileException(e);
        }
        catch (Exception exception)
        {
            throw new CouldNotSaveConfigFileContent(exception);
        }
    }

    public void saveBasicNetworkConfigContent(String content)
    {
        try
        {
            this.serverConfigStorage.saveBasicNetworkConfigFileContent(content, true);
        }
        catch (ParsingException e)
        {
            throw new CouldNotParseFileException(e);
        }
        catch (Exception exception)
        {
            throw new CouldNotSaveConfigFileContent(exception);
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
