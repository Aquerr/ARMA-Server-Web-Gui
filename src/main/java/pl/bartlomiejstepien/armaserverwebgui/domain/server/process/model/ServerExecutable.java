package pl.bartlomiejstepien.armaserverwebgui.domain.server.process.model;

import lombok.Getter;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.SystemUtils;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.SteamArmaBranch;

import java.util.Map;

@Getter
public enum ServerExecutable
{
    MAIN_BRANCH("arma3server", "arma3server_x64"),

    PROFILING_BRANCH("arma3server", "arma3serverprofiling_x64");

    private static final Map<SteamArmaBranch, ServerExecutable> SERVER_EXECUTABLES = Map.of(
            SteamArmaBranch.PUBLIC, MAIN_BRANCH,
            SteamArmaBranch.PROFILING, PROFILING_BRANCH
    );

    private final String serverExecutable;
    private final String serverExecutable64bit;

    ServerExecutable(String serverExecutable, String serverExecutable64bit)
    {
        this.serverExecutable = serverExecutable;
        this.serverExecutable64bit = serverExecutable64bit;
    }

    public static String getForBranch(SteamArmaBranch branch)
    {
        return SERVER_EXECUTABLES.getOrDefault(branch, MAIN_BRANCH).getExecutableForSystem();
    }

    public String getExecutableForSystem()
    {
        var executable = SystemUtils.is64Bit() ? serverExecutable64bit : serverExecutable;
        return SystemUtils.isWindows() ? executable + ".exe" : executable;
    }
}
