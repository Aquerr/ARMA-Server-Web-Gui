package pl.bartlomiejstepien.armaserverwebgui.domain.server.mission.exception;

import org.springframework.http.HttpStatus;
import pl.bartlomiejstepien.armaserverwebgui.application.ApiException;
import pl.bartlomiejstepien.armaserverwebgui.application.ApiExceptionCode;

@ApiException(status = HttpStatus.CONFLICT, code = ApiExceptionCode.MISSION_ALREADY_EXISTS, messageKey = "aswg.mission.already-exists")
public class MissionFileAlreadyExistsException extends RuntimeException
{
    public MissionFileAlreadyExistsException()
    {

    }
}
