package pl.bartlomiejstepien.armaserverwebgui.model;

import lombok.Data;
import pl.bartlomiejstepien.armaserverwebgui.util.cfg.CfgProperty;
import pl.bartlomiejstepien.armaserverwebgui.util.cfg.type.PropertyType;

import java.util.ArrayList;
import java.util.List;

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
    private int maxPlayers = 0;

    @CfgProperty(name = "motd[]", type = PropertyType.STRING_ARRAY)
    private String[] motd = new String[0];

    @CfgProperty(name = "BattlEye", type = PropertyType.INTEGER)
    private int battleEye = 1;

    @CfgProperty(name = "persistent", type = PropertyType.INTEGER)
    private int persistent = 0;

    @CfgProperty(name = "verifySignatures", type = PropertyType.INTEGER)
    private int verifySignatures = 2;

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

            }
        }
    }
}
