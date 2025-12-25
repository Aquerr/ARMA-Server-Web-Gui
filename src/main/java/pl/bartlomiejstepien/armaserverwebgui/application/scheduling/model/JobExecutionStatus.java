package pl.bartlomiejstepien.armaserverwebgui.application.scheduling.model;

public enum JobExecutionStatus
{
    STARTED("STARTED"),
    SUCCESS("SUCCESS"),
    FAILURE("FAILURE");

    private final String status;

    JobExecutionStatus(String status)
    {
        this.status = status;
    }

    public String getStatus()
    {
        return status;
    }
}
