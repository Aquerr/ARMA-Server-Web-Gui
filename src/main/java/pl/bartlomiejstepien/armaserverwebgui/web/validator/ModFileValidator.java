package pl.bartlomiejstepien.armaserverwebgui.web.validator;

import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import pl.bartlomiejstepien.armaserverwebgui.web.exception.NotAllowedFileTypeException;

@Component
public class ModFileValidator implements Validator<MultipartFile>
{
    @Override
    public void validate(MultipartFile multipartFile)
    {
        if (!hasCorrectFileType(multipartFile))
            throw new NotAllowedFileTypeException("Wrong file type! Only .zip files are supported!");
    }

    private boolean hasCorrectFileType(MultipartFile multipartFile)
    {
        String contentType = Optional.ofNullable(multipartFile.getContentType()).orElse("unknown");
        String originalFilename = Optional.ofNullable(multipartFile.getOriginalFilename()).orElse("unknown");

        return contentType.equalsIgnoreCase("application/zip")
                || originalFilename.endsWith(".zip");
    }
}
