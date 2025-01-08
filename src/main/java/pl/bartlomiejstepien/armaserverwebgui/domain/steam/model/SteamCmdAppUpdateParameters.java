package pl.bartlomiejstepien.armaserverwebgui.domain.steam.model;

import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
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
    private String branch;

    public List<String> asExecutionParameters()
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

        if (branch != null)
        {
            params.add("-beta " + branch);
        }

        params.addAll(List.of(
                String.valueOf(appId),
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
