package pl.bartlomiejstepien.armaserverwebgui.domain.server.process.model;

public enum ServerExecutable
{
    MAIN_BRANCH("arma3server", "arma3server_x64"),

    PROFILING_BRANCH("arma3server", "arma3serverprofiling_x64");

    private final String serverExecutable;
    private final String getServerExecutable64bit;

    ServerExecutable(String serverExecutable, String getServerExecutable64bit)
    {
        this.serverExecutable = serverExecutable;
        this.getServerExecutable64bit = getServerExecutable64bit;
    }

    public String getServerExecutable()
    {
        return serverExecutable;
    }

    public String getGetServerExecutable64bit()
    {
        return getServerExecutable64bit;
    }
}
