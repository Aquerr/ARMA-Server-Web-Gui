package pl.bartlomiejstepien.armaserverwebgui.domain.steam.model;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class SteamCmdAppUpdateParameters
{
    private int appId;
    private String serverDirectoryPath;
    private String steamCmdPath;
    private String steamUsername;
    private String steamPassword;

    public List<String> asExecutionParameters()
    {
        return List.of(
                steamCmdPath,
                "+force_install_dir",
                serverDirectoryPath,
                "+login",
                steamUsername,
                steamPassword,
                "+app_update",
                String.valueOf(appId),
                "validate",
                "+quit"
        );
    }

    @Override
    public String toString()
    {
        return "SteamCmdAppUpdateParameters{" +
                "appId=" + appId +
                ", serverDirectoryPath='" + serverDirectoryPath + '\'' +
                ", steamCmdPath='" + steamCmdPath + '\'' +
                ", steamUsername='" + steamUsername + '\'' +
                ", steamPassword='XXX'}";
    }
}
