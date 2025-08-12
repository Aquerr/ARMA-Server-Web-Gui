package pl.bartlomiejstepien.armaserverwebgui.domain.server.process;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@ConditionalOnExpression("#{!T(pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.SystemUtils).isWindows()}")
@Component
@Slf4j
public class UnixProcessAliveChecker implements ProcessAliveChecker
{
    @Override
    public boolean isPidAlive(long processId)
    {
        if (processId <= 0)
            return false;

        ProcessBuilder processBuilder = new ProcessBuilder("sh", "-c", "ps -p " + processId);

        try
        {
            Process process = processBuilder.start();
            InputStreamReader isReader = new InputStreamReader(process.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(isReader);
            String strLine = null;
            boolean isPidRunning = false;
            while ((strLine = bufferedReader.readLine()) != null)
            {
                if (strLine.contains(" " + processId + " "))
                {
                    isPidRunning = true;
                    break;
                }
            }

            process.destroy();
            log.info("Is pid = {} alive: {}", processId, isPidRunning);
            return isPidRunning;

        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
    }
}
