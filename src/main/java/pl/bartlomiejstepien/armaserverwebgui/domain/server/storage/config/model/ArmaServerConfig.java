package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.annotation.CfgProperty;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.annotation.ClassList;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.annotation.ClassName;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.type.PropertyType;

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

    @CfgProperty(name = "motd[]", type = PropertyType.ARRAY_OF_STRINGS)
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

    @CfgProperty(name = "forcedDifficulty", type = PropertyType.QUOTED_STRING)
    private String forcedDifficulty = "";

    @CfgProperty(name = "headlessClients[]", type = PropertyType.ARRAY_OF_STRINGS)
    private String[] headlessClients = {};

    @CfgProperty(name = "localClient[]", type = PropertyType.ARRAY_OF_STRINGS)
    private String[] localClients = {};

    @CfgProperty(name = "filePatchingExceptions[]", type = PropertyType.ARRAY_OF_STRINGS)
    private String[] filePatchingExceptions = {};

    @CfgProperty(name = "allowedLoadFileExtensions[]", type = PropertyType.ARRAY_OF_STRINGS)
    private String[] allowedLoadFileExtensions = {"hpp", "sqs", "sqf", "fsm", "cpp", "paa", "txt", "xml", "inc", "ext", "sqm", "ods",
            "fxy", "lip", "csv", "kb", "bik", "bikb", "html", "htm", "biedi"};

    @CfgProperty(name = "admins[]", type = PropertyType.ARRAY_OF_STRINGS)
    private String[] admins = {};

    @CfgProperty(name = "allowedVoteCmds[]", type = PropertyType.ARRAY_OF_NO_FIELDS_OBJECT)
    private VoteCmd[] allowedVoteCmds = {};

    @CfgProperty(name = "voteThreshold", type = PropertyType.RAW_STRING)
    private String voteThreshold = "0.5";

    @CfgProperty(name = "voteMissionPlayers", type = PropertyType.INTEGER)
    private int voteMissionPlayers = 1;

    @CfgProperty(name = "kickduplicate", type = PropertyType.INTEGER)
    private int kickDuplicate = 0;

    @CfgProperty(name = "kickTimeout[]", type = PropertyType.ARRAY_OF_NO_FIELDS_OBJECT)
    private KickTimeout[] kickTimeouts = {
            KickTimeout.builder().kickId(0).timeout(60).build(),
            KickTimeout.builder().kickId(1).timeout(60).build(),
            KickTimeout.builder().kickId(2).timeout(60).build(),
            KickTimeout.builder().kickId(3).timeout(60).build()
    };

    @CfgProperty(name = "Missions", type = PropertyType.CLASS, isClass = true)
    @ClassList
    private List<Missions.Mission> missions = new ArrayList<>();

    @Data
    public static class Missions
    {
        @Data
        public static class Mission
        {
            @ClassName
            @CfgProperty(name = "template", type = PropertyType.QUOTED_STRING)
            private String template;
            @CfgProperty(name = "difficulty", type = PropertyType.QUOTED_STRING)
            private String difficulty;
            @CfgProperty(name = "params", type = PropertyType.CLASS, isClass = true)
            private Map<String, String> params = new HashMap<>();
        }
    }
}
