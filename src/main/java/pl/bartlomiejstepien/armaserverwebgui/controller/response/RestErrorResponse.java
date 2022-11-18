package pl.bartlomiejstepien.armaserverwebgui.controller.response;

import lombok.Value;

@Value(staticConstructor = "of")
public class RestErrorResponse
{
    String message;
    int code;
}
