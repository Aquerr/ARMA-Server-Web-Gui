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

    public String asString()
    {
        return String.join(" ", asList());
    }

    public List<String> asList()
    {
        List<String> args = new ArrayList<>();
        if (!SystemUtils.isWindows())
        {
            args.add("nohup");
        }

        args.add(executablePath);
        args.addAll(getArmaServerArgs());

        if (!SystemUtils.isWindows())
        {
            args.add("&");
        }
        return args;
    }

    private List<String> getArmaServerArgs()
    {
        List<String> args = new ArrayList<>();
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
        return args;
    }
}
