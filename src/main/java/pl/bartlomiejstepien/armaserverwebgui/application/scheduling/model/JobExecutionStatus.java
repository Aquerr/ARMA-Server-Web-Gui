package pl.bartlomiejstepien.armaserverwebgui.application.scheduling.model;

import java.util.Arrays;
import java.util.Optional;

public enum JobExecutionStatus
{
    STARTED("STARTED"),
    SUCCESS("SUCCESS"),
    FAILURE("FAILURE");

    private final String code;

    JobExecutionStatus(String code)
    {
        this.code = code;
    }

    public String getCode()
    {
        return code;
    }

    public static Optional<JobExecutionStatus> findByCode(String code)
    {
        return Arrays.stream(values())
                .filter(enumStatus -> code.equals(enumStatus.getCode()))
                .findFirst();
    }
}
