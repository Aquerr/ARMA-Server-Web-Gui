package pl.bartlomiejstepien.armaserverwebgui.domain.server.process.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.util.StringUtils;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.SystemUtils;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@ToString
@Builder
public class ArmaServerParameters
{
    private final String serverConfigPath;
    private final String networkConfigPath;
    @Builder.Default
    private final int port = 2302;
    @Builder.Default
    private final String profileName = "player";
    private final String executablePath;
    @Builder.Default
    private final Set<String> mods = new HashSet<>();
    @Builder.Default
    private final Set<String> serverMods = new HashSet<>();

    private final boolean customModSettings;

    private final String serverDirectory;

    private String overrideParameters;

    public String asString()
    {
        return String.join(" ", asList());
    }

    public List<String> asList()
    {
        if (StringUtils.hasText(overrideParameters))
        {
            return Arrays.stream(overrideParameters.split(" ")).toList();
        }

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

        if (customModSettings)
        {
            args.add("-filePatching");
        }

        args.add("-port=" + port);
        args.add("\"-cfg=" + networkConfigPath + "\"");
        args.add("\"-config=" + serverConfigPath + "\"");

        if (SystemUtils.isWindows())
        {
            args.add("\"-profiles=" + Paths.get(serverDirectory).resolve("aswg_profiles").toAbsolutePath() + "\"");
        }

        if (StringUtils.hasText(profileName))
        {
            args.add("\"-name=" + profileName + "\"");
        }
        if (!mods.isEmpty())
        {
            args.add("\"-mod=" + String.join(";", mods) + "\"");
        }
        if (!serverMods.isEmpty())
        {
            args.add("\"-serverMod=" + String.join(";", serverMods) + "\"");
        }
        return args;
    }
}
