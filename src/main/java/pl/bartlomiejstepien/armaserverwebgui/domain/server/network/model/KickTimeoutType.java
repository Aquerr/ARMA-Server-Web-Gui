package pl.bartlomiejstepien.armaserverwebgui.domain.server.network.model;

import java.util.Arrays;

public enum KickTimeoutType
{
    MANUAL_KICK(0),
    CONNECTIVITY_KICK(1),
    BATTL_EYE_KICK(2),
    HARMLESS_KICK(3);

    private final int kickId;

    KickTimeoutType(int kickId)
    {
        this.kickId = kickId;
    }

    public static KickTimeoutType findByKickId(int kickId)
    {
        return Arrays.stream(values())
                .filter(type -> type.getKickId() == kickId)
                .findFirst()
                .orElseThrow();
    }

    public int getKickId()
    {
        return kickId;
    }
}
