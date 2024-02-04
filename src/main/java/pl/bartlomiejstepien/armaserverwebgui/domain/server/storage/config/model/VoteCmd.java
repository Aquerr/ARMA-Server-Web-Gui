package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.model;

import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.CfgProperty;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.type.PropertyType;

public class VoteCmd
{
    @CfgProperty(name = "commandName", type = PropertyType.QUOTED_STRING)
    private String commandName;

    @CfgProperty(name = "preMissionStart", type = PropertyType.RAW_STRING)
    private boolean preMissionStart = true;

    @CfgProperty(name = "postMissionStart", type = PropertyType.RAW_STRING)
    private boolean postMissionStart = true;

    @CfgProperty(name = "votingThreshold", type = PropertyType.RAW_STRING)
    private double votingThreshold = 0.5;

    @CfgProperty(name = "percentSideVotingThreshold", type = PropertyType.RAW_STRING)
    private double percentSideVotingThreshold = 0.5;
}
