package pl.bartlomiejstepien.armaserverwebgui.domain.steam;

import lombok.extern.slf4j.Slf4j;
import pl.bartlomiejstepien.armaserverwebgui.application.process.ExternalProcess;

@Slf4j
public class SteamCmdExternalProcess extends ExternalProcess
{
    private static final int HEALTH_CHECK_INTERVAL_MILLIS = 5000;
    private static final int HEALTH_CHECK_MAX_ATTEMPTS = 3;

    private static final String DOWNLOADING_ITEM_LOG = "Downloading item";

    private int healthCheckAttempts = 0;
    private String lastCheckedLog;
    private Thread healthCheckThread;

    @Override
    protected void handleHealthCheck()
    {
        healthCheckThread = Thread.ofVirtual().start(this::performHealthCheck);
    }

    @Override
    protected void postClose()
    {
        lastCheckedLog = null;
        if (healthCheckThread != null)
        {
            healthCheckThread.interrupt();
            healthCheckThread = null;
        }
    }

    private void performHealthCheck()
    {
        while (!Thread.currentThread().isInterrupted())
        {
            try
            {
                Thread.sleep(HEALTH_CHECK_INTERVAL_MILLIS);
                log.info("Checking process health: {}", process.pid());
                boolean isHang = isProcessHang();
                if (isHang)
                {
                    log.warn("SteamCmd process is hanged. Destroying it.");
                    process.destroy();
                }
            }
            catch (InterruptedException e)
            {
                break;
            }
        }
    }

    private boolean isProcessHang()
    {
        healthCheckAttempts++;

        String lastLog = logs.isEmpty() ? "" : logs.getLast();
        log.info("Last log: {}, new log: {}", lastCheckedLog, lastLog);
        if (!lastLog.equals(lastCheckedLog))
        {
            lastCheckedLog = lastLog;
            healthCheckAttempts = 0;
            return false;
        }

        if (lastLog.startsWith(DOWNLOADING_ITEM_LOG))
        {
            healthCheckAttempts = 0;
            return false;
        }

        return healthCheckAttempts >= HEALTH_CHECK_MAX_ATTEMPTS;
    }
}
