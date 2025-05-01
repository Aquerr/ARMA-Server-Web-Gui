package pl.bartlomiejstepien.armaserverwebgui.web.validator;

import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import pl.bartlomiejstepien.armaserverwebgui.web.exception.NotAllowedFileTypeException;

@Component
public class MissionFileValidator implements Validator<MultipartFile>
{
    @Override
    public void validate(MultipartFile filePart)
    {
        if (!hasCorrectFileType(filePart))
            throw new NotAllowedFileTypeException("Wrong file type! Only .pbo files are supported!");
    }

    private boolean hasCorrectFileType(MultipartFile multipartFile)
    {
        return Optional.ofNullable(multipartFile.getOriginalFilename())
                .orElse("unknown")
                .endsWith(".pbo");
    }
}
