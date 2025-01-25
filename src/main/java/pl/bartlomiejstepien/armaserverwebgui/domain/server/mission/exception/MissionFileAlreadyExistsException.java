package pl.bartlomiejstepien.armaserverwebgui.domain.server.mission.exception;

import org.springframework.http.HttpStatus;
import pl.bartlomiejstepien.armaserverwebgui.application.ApiException;

@ApiException(status = HttpStatus.CONFLICT, messageKey = "aswg.mission.already-exists")
public class MissionFileAlreadyExistsException extends RuntimeException
{
    public MissionFileAlreadyExistsException()
    {

    }
}
