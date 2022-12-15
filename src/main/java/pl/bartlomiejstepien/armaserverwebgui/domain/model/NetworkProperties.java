package pl.bartlomiejstepien.armaserverwebgui.domain.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class NetworkProperties
{
    private boolean upnp;
    private int maxPing;
    private boolean loopback;
    private int disconnectTimeout;
    private int maxDesync;
    private int maxPacketLoss;
}
