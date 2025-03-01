package pl.bartlomiejstepien.armaserverwebgui.web.response;

import lombok.Value;

@Value(staticConstructor = "of")
public class RestErrorResponse
{
    String message;
    String code;
    int status;
}
