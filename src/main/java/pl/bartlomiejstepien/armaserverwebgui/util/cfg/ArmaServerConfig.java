package pl.bartlomiejstepien.armaserverwebgui.util.cfg;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ArmaServerConfig
{
    @CfgProperty(name = "hostname", type = String.class)
    private String hostname;

    @CfgProperty(name = "password", type = String.class)
    private String password;

    @CfgProperty(name = "passwordAdmin", type = String.class)
    private String passwordAdmin;

    @CfgProperty(name = "serverCommandPassword", type = String.class)
    private String serverCommandPassword;

    @CfgProperty(name = "logFile", type = String.class)
    private String logFile;

    @CfgProperty(name = "maxPlayers", type = Integer.class)
    private int maxPlayers;

    @CfgProperty(name = "motd[]", type = String[].class)
    private String[] motd;

    @CfgProperty(name = "Missions", type = Missions.class)
    private Missions missions;

    @Data
    public static class Missions
    {
        private List<Mission> missions = new ArrayList<>();

        @Data
        public static class Mission
        {
            @CfgProperty(name = "template", type = String.class)
            private String template;
            @CfgProperty(name = "difficulty", type = String.class)
            private String difficulty;
            @CfgProperty(name = "params", type = Params.class)
            private Params params;

            @Data
            public static class Params
            {

            }
        }
    }
}
