package pl.bartlomiejstepien.armaserverwebgui.domain.server.security.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoteCommand
{
    private String name;
    private boolean allowedPreMission = true;
    private boolean allowedPostMission = true;
    private double votingThreshold = 0.5;
    private double percentageSideVotingThreshold = 0.5;
}
