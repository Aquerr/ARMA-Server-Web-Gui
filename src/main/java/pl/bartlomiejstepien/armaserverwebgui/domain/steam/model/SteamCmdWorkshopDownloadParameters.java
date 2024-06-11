package pl.bartlomiejstepien.armaserverwebgui.domain.steam.model;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class SteamCmdWorkshopDownloadParameters
{
    private long fileId;
    private String title;
    private int appId;
    private String steamCmdPath;
    private String steamUsername;
    private String steamPassword;

    public List<String> asExecutionParameters()
    {
        return List.of(
                steamCmdPath,
                "+login",
                steamUsername,
                steamPassword,
                "+workshop_download_item",
                String.valueOf(appId),
                String.valueOf(fileId),
                "+quit"
        );
    }

    @Override
    public String toString()
    {
        return "SteamCmdWorkshopDownloadParameters{" +
                "fileId=" + fileId +
                ", title='" + title + '\'' +
                ", appId=" + appId +
                ", steamCmdPath='" + steamCmdPath + '\'' +
                ", steamUsername='" + steamUsername + '\'' +
                ", steamPassword='XXX'}";
    }
}
