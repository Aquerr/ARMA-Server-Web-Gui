package pl.bartlomiejstepien.armaserverwebgui.domain.server.mission.exception;

public class MissionDoesNotExistException extends RuntimeException
{
    public MissionDoesNotExistException(String message)
    {
        super(message);
    }
}
