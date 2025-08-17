package pl.bartlomiejstepien.armaserverwebgui.domain.steam.model;

import lombok.Builder;
import lombok.Getter;
import pl.bartlomiejstepien.armaserverwebgui.application.process.ProcessParameters;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.SteamArmaBranch;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class SteamCmdAppUpdateParameters implements ProcessParameters
{
    private int appId;
    private String serverDirectoryPath;
    private String steamCmdPath;
    private String steamUsername;
    private String steamPassword;
    private SteamArmaBranch branch;

    @Override
    public List<String> asProcessParameters()
    {
        final List<String> params = new ArrayList<>(List.of(
                steamCmdPath,
                "+force_install_dir",
                serverDirectoryPath,
                "+login",
                steamUsername,
                steamPassword,
                "+app_update"
        ));

        if (SteamArmaBranch.PUBLIC != branch)
        {
            params.add("\"" + appId + " -beta " + branch.getCode() + "\"");
        }
        else
        {
            params.add(String.valueOf(appId));
        }

        params.addAll(List.of(
                "validate",
                "+quit"
        ));

        return params;
    }

    @Override
    public String toString()
    {
        return "SteamCmdAppUpdateParameters{" +
                "appId=" + appId +
                ", branch=" + branch +
                ", serverDirectoryPath='" + serverDirectoryPath + '\'' +
                ", steamCmdPath='" + steamCmdPath + '\'' +
                ", steamUsername='" + steamUsername + '\'' +
                ", steamPassword='XXX'}";
    }
}
