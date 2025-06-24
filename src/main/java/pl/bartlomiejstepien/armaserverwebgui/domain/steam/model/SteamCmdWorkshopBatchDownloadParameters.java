package pl.bartlomiejstepien.armaserverwebgui.domain.steam.model;

import lombok.Builder;
import lombok.Getter;
import pl.bartlomiejstepien.armaserverwebgui.application.process.ProcessParameters;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class SteamCmdWorkshopBatchDownloadParameters implements ProcessParameters
{
    private List<Long> fileIds;
    private List<String> titles;
    private int appId;
    private String steamCmdPath;
    private String steamUsername;
    private String steamPassword;

    @Override
    public List<String> asProcessParameters()
    {
        List<String> parameters = new ArrayList<>();
        parameters.add(steamCmdPath);
        parameters.add("+login");
        parameters.add(steamUsername);
        parameters.add(steamPassword);

        for (Long fileId : fileIds)
        {
            parameters.add("+workshop_download_item");
            parameters.add(String.valueOf(appId));
            parameters.add(String.valueOf(fileId));
        }

        parameters.add("+quit");
        return parameters;
    }

    @Override
    public String toString()
    {
        return "SteamCmdWorkshopDownloadParameters{" +
                "fileId=" + fileIds +
                ", title='" + titles + '\'' +
                ", appId=" + appId +
                ", steamCmdPath='" + steamCmdPath + '\'' +
                ", steamUsername='" + steamUsername + '\'' +
                ", steamPassword='XXX'}";
    }
}
