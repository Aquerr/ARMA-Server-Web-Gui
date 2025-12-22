package pl.bartlomiejstepien.armaserverwebgui.domain.server.network.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private KickTimeouts kickTimeouts;

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

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KickTimeouts
    {
        private int manualKickTimeoutSeconds;
        private int connectivityKickTimeoutSeconds;
        private int battlEyeKickTimeoutSeconds;
        private int harmlessKickTimeoutSeconds;
    }
}
