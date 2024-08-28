package pl.bartlomiejstepien.armaserverwebgui.domain.server.network.model;

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
    private boolean enablePlayerDiag;
    private int steamProtocolMaxDataSize;

    // Performance properties
    private long minBandwidth;
    private long maxBandwidth;
    private int maxMsgSend;
    private int maxSizeGuaranteed;
    private int maxSizeNonGuaranteed;
    private String minErrorToSend;
    private String minErrorToSendNear;
    private int maxCustomFileSize;
    private int maxPacketSize;
}
