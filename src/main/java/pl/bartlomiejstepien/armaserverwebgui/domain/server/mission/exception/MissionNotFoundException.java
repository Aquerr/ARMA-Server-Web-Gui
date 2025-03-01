package pl.bartlomiejstepien.armaserverwebgui.domain.server.mission.exception;

import org.springframework.http.HttpStatus;
import pl.bartlomiejstepien.armaserverwebgui.application.ApiException;
import pl.bartlomiejstepien.armaserverwebgui.application.ApiExceptionCode;

@ApiException(status = HttpStatus.NOT_FOUND, code = ApiExceptionCode.MISSION_NOT_FOUND, messageKey = "aswg.mission.not-found")
public class MissionNotFoundException extends RuntimeException
{
    public MissionNotFoundException(String message)
    {
        super(message);
    }
}
