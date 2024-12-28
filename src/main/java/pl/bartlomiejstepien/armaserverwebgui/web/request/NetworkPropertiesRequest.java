package pl.bartlomiejstepien.armaserverwebgui.web.request;

import lombok.Builder;
import lombok.Data;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.network.model.NetworkProperties;

@Builder
@Data
public class NetworkPropertiesRequest
{
    private boolean upnp;
    private int maxPing;
    private boolean loopback;
    private int disconnectTimeout;
    private int maxDesync;
    private int maxPacketLoss;
    private boolean enablePlayerDiag;
    private int steamProtocolMaxDataSize;
    private NetworkProperties.KickTimeouts kickTimeouts;

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