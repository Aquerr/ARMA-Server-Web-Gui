package pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.dotnet;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public final class DotnetDateTimeUtils
{
    public static OffsetDateTime dotnetTicksToOffsetDateTime(final long fromBytes)
    {
        // Mask out kind and ticks
        int kind = Math.toIntExact((fromBytes >> 62) & 0x3);
        long ticks = fromBytes & 0x3FFF_FFFF_FFFF_FFFFL;
        LocalDateTime cSharpEpoch = LocalDate.of(1, Month.JANUARY, 1).atStartOfDay();
        // 100 nanosecond units or 10^-7 seconds
        final int unitsPerSecond = 10_000_000;
        long seconds = ticks / unitsPerSecond;
        long nanos = (ticks % unitsPerSecond) * 100;
        LocalDateTime localDateTime = cSharpEpoch.plusSeconds(seconds).plusNanos(nanos);

        if (kind > 2 || kind < 0) {
            throw new IllegalArgumentException("Invalid ticks kind: " + kind);
        }

        return OffsetDateTime.of(localDateTime, ZoneOffset.UTC);
    }

    private DotnetDateTimeUtils()
    {

    }
}
