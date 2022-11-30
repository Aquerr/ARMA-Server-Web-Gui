package pl.bartlomiejstepien.armaserverwebgui.domain.server.general;

import pl.bartlomiejstepien.armaserverwebgui.domain.model.GeneralProperties;

public interface GeneralService
{
    GeneralProperties getGeneralProperties();

    void saveGeneralProperties(GeneralProperties generalProperties);
}
