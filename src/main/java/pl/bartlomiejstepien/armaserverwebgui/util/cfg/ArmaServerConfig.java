package pl.bartlomiejstepien.armaserverwebgui.util.cfg;

import lombok.Data;
import pl.bartlomiejstepien.armaserverwebgui.util.cfg.type.PropertyType;

import java.util.ArrayList;
import java.util.List;

@Data
public class ArmaServerConfig
{
    @CfgProperty(name = "hostname", type = PropertyType.QUOTED_STRING)
    private String hostname;

    @CfgProperty(name = "password", type = PropertyType.QUOTED_STRING)
    private String password;

    @CfgProperty(name = "passwordAdmin", type = PropertyType.QUOTED_STRING)
    private String passwordAdmin;

    @CfgProperty(name = "serverCommandPassword", type = PropertyType.QUOTED_STRING)
    private String serverCommandPassword;

    @CfgProperty(name = "logFile", type = PropertyType.QUOTED_STRING)
    private String logFile;

    @CfgProperty(name = "maxPlayers", type = PropertyType.INTEGER)
    private int maxPlayers;

    @CfgProperty(name = "motd[]", type = PropertyType.STRING_ARRAY)
    private String[] motd;

    @CfgProperty(name = "Missions", type = PropertyType.MISSIONS)
    private Missions missions;

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
            @CfgProperty(name = "params", type = PropertyType.PARAMS)
            private Params params;

            @Data
            public static class Params
            {

            }
        }
    }
}
