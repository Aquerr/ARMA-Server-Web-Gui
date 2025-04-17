package pl.bartlomiejstepien.armaserverwebgui.domain.server.process.model;

import lombok.Getter;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.SystemUtils;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.SteamUtils;

@Getter
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

    public static String getForBranch(String branch)
    {
        String executable;
        if (SteamUtils.ARMA_BRANCH_PROFILING.equals(branch))
        {
            executable = is64Bit() ? ServerExecutable.PROFILING_BRANCH.getGetServerExecutable64bit() : ServerExecutable.PROFILING_BRANCH.getServerExecutable();
        }
        else
        {
            executable = is64Bit() ? ServerExecutable.MAIN_BRANCH.getGetServerExecutable64bit() : ServerExecutable.MAIN_BRANCH.getServerExecutable();
        }

        return SystemUtils.isWindows() ? executable + ".exe" : executable;
    }

    private static boolean is64Bit()
    {
        return System.getProperty("os.arch").contains("64");
    }
}
