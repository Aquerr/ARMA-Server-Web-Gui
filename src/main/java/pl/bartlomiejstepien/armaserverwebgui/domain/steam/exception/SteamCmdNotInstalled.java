package pl.bartlomiejstepien.armaserverwebgui.domain.steam.exception;

import org.springframework.http.HttpStatus;
import pl.bartlomiejstepien.armaserverwebgui.application.ApiException;
import pl.bartlomiejstepien.armaserverwebgui.application.ApiExceptionCode;

@ApiException(status = HttpStatus.INTERNAL_SERVER_ERROR, code = ApiExceptionCode.STEAM_CMD_NOT_INSTALLED, messageKey = "aswg.steam.not-installed")
public class SteamCmdNotInstalled extends RuntimeException
{

}
