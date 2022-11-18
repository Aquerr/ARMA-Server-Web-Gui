package pl.bartlomiejstepien.armaserverwebgui.controller.validator;

import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import pl.bartlomiejstepien.armaserverwebgui.exception.NotAllowedFileTypeException;

@Component
public class MissionFileValidator implements Validator<FilePart>
{
    @Override
    public void validate(FilePart filePart)
    {
        if (!hasCorrectFileType(filePart))
            throw new NotAllowedFileTypeException("Wrong file type! Only .pbo files are supported!");
    }

    private boolean hasCorrectFileType(FilePart filePart)
    {
        return filePart.filename().endsWith(".pbo");
    }
}
