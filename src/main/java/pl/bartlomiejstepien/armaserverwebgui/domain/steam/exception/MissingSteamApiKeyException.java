package pl.bartlomiejstepien.armaserverwebgui.domain.steam.exception;

import org.springframework.http.HttpStatus;
import pl.bartlomiejstepien.armaserverwebgui.application.ApiException;
import pl.bartlomiejstepien.armaserverwebgui.application.ApiExceptionCode;

@ApiException(
        status = HttpStatus.BAD_REQUEST,
        code = ApiExceptionCode.STEAM_API_KEY_MISSING,
        messageKey = "aswg.steam.api-key-missing"
)
public class MissingSteamApiKeyException extends RuntimeException
{

}
