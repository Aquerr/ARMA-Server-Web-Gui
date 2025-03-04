package pl.bartlomiejstepien.armaserverwebgui.application.tracing;

public enum HttpTracingFields
{
    URI("uri"),
    METHOD("method"),
    USER_ID("user-id"),
    USER_AGENT("user-agent"),
    DURATION("duration"),
    BODY("body"),
    RESPONSE_BODY("response-body"),
    CONTENT_TYPE("content-type"),
    RESPONSE_CONTENT_TYPE("response-content-type"),
    STATUS("status"),
    CORRELATION_ID("correlation-id")
    ;

    private final String fieldName;

    HttpTracingFields(String fieldName)
    {
        this.fieldName = fieldName;
    }

    public String getFieldName()
    {
        return fieldName;
    }
}
