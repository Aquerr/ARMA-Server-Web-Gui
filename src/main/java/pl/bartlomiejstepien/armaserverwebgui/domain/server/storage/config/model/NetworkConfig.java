package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.annotation.CfgProperty;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.type.PropertyType;

@Data
public class NetworkConfig
{
    @CfgProperty(name = "MinBandwidth", type = PropertyType.INTEGER)
    private long minBandwidth = 131072;

    @CfgProperty(name = "MaxBandwidth", type = PropertyType.LONG)
    private long maxBandwidth = 10000000000L;

    @CfgProperty(name = "MaxMsgSend", type = PropertyType.INTEGER)
    private int maxMsgSend = 128;

    @CfgProperty(name = "MaxSizeGuaranteed", type = PropertyType.INTEGER)
    private int maxSizeGuaranteed = 512;

    @CfgProperty(name = "MaxSizeNonguaranteed", type = PropertyType.INTEGER)
    private int maxSizeNonGuaranteed = 256;

    @CfgProperty(name = "MinErrorToSend", type = PropertyType.RAW_STRING)
    private String minErrorToSend = "0.001";

    @CfgProperty(name = "MinErrorToSendNear", type = PropertyType.RAW_STRING)
    private String minErrorToSendNear = "0.01";

    @CfgProperty(name = "MaxCustomFileSize", type = PropertyType.INTEGER)
    private int maxCustomFileSize = 0;

    @CfgProperty(name = "sockets", isClass = true, type = PropertyType.CLASS)
    private Sockets sockets = new Sockets();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Sockets
    {
        @CfgProperty(name = "maxPacketSize", type = PropertyType.INTEGER)
        private int maxPacketSize = 1400;
    }
}
