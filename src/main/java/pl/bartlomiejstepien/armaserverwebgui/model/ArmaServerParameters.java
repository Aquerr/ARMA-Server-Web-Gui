package pl.bartlomiejstepien.armaserverwebgui.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.util.StringUtils;
import pl.bartlomiejstepien.armaserverwebgui.util.SystemUtils;

import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
@Builder
public class ArmaServerParameters
{
    private final String configPath;
    @Builder.Default
    private final int port = 2302;
    private final String serverName;
    private final String executablePath;
    @Builder.Default
    private final List<String> mods = new ArrayList<>();

    private final String serverDirectory;

    public List<String> asList()
    {
        List<String> args = new ArrayList<>();
        if (!SystemUtils.isWindows())
        {
            args.add("nohup");
        }

        args.add(executablePath);
        args.add("-port=" + port);
        args.add("-config=" + configPath);

        if (StringUtils.hasText(serverName))
        {
            args.add("-name=" + serverName);
        }
        if (!mods.isEmpty())
        {
            args.add("-mod=" + String.join(";", mods));
        }

        if (!SystemUtils.isWindows())
        {
            args.add("&");
        }
        return args;
    }
}
