package pl.bartlomiejstepien.armaserverwebgui.controller.validator;

import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;

@Component
public class ModFileValidator
{
    public boolean isValid(FilePart filePart)
    {
        return filePart.filename().endsWith(".zip");
    }
}
