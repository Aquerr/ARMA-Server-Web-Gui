package pl.bartlomiejstepien.armaserverwebgui.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.util.StringUtils;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.util.SystemUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private final Set<String> mods = new HashSet<>();
    @Builder.Default
    private final Set<String> serverMods = new HashSet<>();

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
        args.add("\"-config=" + configPath + "\"");

        if (StringUtils.hasText(serverName))
        {
            args.add("\"-name=" + serverName + "\"");
        }
        if (!mods.isEmpty())
        {
            args.add("\"-mod=" + String.join(";", mods) + "\"");
        }
        if (!serverMods.isEmpty())
        {
            args.add("\"-serverMod=" + String.join(";", mods) + "\"");
        }
        return args;
    }
}
