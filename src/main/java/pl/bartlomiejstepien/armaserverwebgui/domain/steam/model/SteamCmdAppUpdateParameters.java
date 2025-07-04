package pl.bartlomiejstepien.armaserverwebgui.domain.steam.model;

import lombok.Builder;
import lombok.Getter;
import pl.bartlomiejstepien.armaserverwebgui.application.process.ProcessParameters;

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
    private String branch;

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

        if (branch != null && !branch.equals("public"))
        {
            params.add("\"" + appId + " -beta " + branch + "\"");
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
