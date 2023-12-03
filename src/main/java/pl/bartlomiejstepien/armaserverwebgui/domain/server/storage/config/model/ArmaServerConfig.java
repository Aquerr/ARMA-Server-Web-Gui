package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.model;

import lombok.Data;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.CfgProperty;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.type.PropertyType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class ArmaServerConfig
{
    @CfgProperty(name = "hostname", type = PropertyType.QUOTED_STRING)
    private String hostname = "";

    @CfgProperty(name = "password", type = PropertyType.QUOTED_STRING)
    private String password = "";

    @CfgProperty(name = "passwordAdmin", type = PropertyType.QUOTED_STRING)
    private String passwordAdmin = "";

    @CfgProperty(name = "serverCommandPassword", type = PropertyType.QUOTED_STRING)
    private String serverCommandPassword = "";

    @CfgProperty(name = "logFile", type = PropertyType.QUOTED_STRING)
    private String logFile = "";

    @CfgProperty(name = "maxPlayers", type = PropertyType.INTEGER)
    private int maxPlayers = 64;

    @CfgProperty(name = "motd[]", type = PropertyType.STRING_ARRAY)
    private String[] motd = new String[0];

    @CfgProperty(name = "motdInterval", type = PropertyType.INTEGER)
    private int motdInterval = 5; // seconds

    @CfgProperty(name = "BattlEye", type = PropertyType.INTEGER)
    private int battleEye = 1;

    @CfgProperty(name = "persistent", type = PropertyType.INTEGER)
    private int persistent = 0;

    @CfgProperty(name = "verifySignatures", type = PropertyType.INTEGER)
    private int verifySignatures = 2;

    @CfgProperty(name = "allowedFilePatching", type = PropertyType.INTEGER)
    private int allowedFilePatching = 0; // 0 = Not allowed

    @CfgProperty(name = "upnp", type = PropertyType.RAW_STRING)
    private String upnp = "false";

    @CfgProperty(name = "maxping", type = PropertyType.INTEGER)
    private int maxPing = 500;

    @CfgProperty(name = "loopback", type = PropertyType.RAW_STRING)
    private String loopback = "false";

    @CfgProperty(name = "disconnectTimeout", type = PropertyType.INTEGER)
    private int disconnectTimeout = 90;

    @CfgProperty(name = "maxdesync", type = PropertyType.INTEGER)
    private int maxdesync = 150;

    @CfgProperty(name = "maxpacketloss", type = PropertyType.INTEGER)
    private int maxpacketloss = 150;

    @CfgProperty(name = "enablePlayerDiag", type = PropertyType.INTEGER)
    private int enablePlayerDiag = 0;

    @CfgProperty(name = "steamProtocolMaxDataSize", type = PropertyType.INTEGER)
    private int steamProtocolMaxDataSize = 1024;

    @CfgProperty(name = "drawingInMap", type = PropertyType.RAW_STRING)
    private String drawingInMap = "true";

    @CfgProperty(name = "Missions", type = PropertyType.MISSIONS, isClass = true)
    private Missions missions = new Missions();

    @Data
    public static class Missions
    {
        private List<Mission> missions = new ArrayList<>();

        @Data
        public static class Mission
        {
            @CfgProperty(name = "template", type = PropertyType.RAW_STRING)
            private String template;
            @CfgProperty(name = "difficulty", type = PropertyType.QUOTED_STRING)
            private String difficulty;
            @CfgProperty(name = "params", type = PropertyType.PARAMS, isClass = true)
            private Params params = new Params();

            @Data
            public static class Params
            {
                private Map<String, String> params = new HashMap<>();
            }
        }
    }
}
