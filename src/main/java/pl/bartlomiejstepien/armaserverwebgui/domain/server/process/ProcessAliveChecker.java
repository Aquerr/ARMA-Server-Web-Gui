package pl.bartlomiejstepien.armaserverwebgui.domain.server.process;

@FunctionalInterface
public interface ProcessAliveChecker
{
    boolean isPidAlive(long pid);
}
