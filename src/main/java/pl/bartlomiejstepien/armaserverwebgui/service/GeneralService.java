package pl.bartlomiejstepien.armaserverwebgui.service;

import pl.bartlomiejstepien.armaserverwebgui.model.GeneralProperties;

public interface GeneralService
{
    GeneralProperties getGeneralProperties();

    void saveGeneralProperties(GeneralProperties generalProperties);
}
