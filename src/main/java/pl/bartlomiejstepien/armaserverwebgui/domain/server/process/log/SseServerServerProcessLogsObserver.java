package pl.bartlomiejstepien.armaserverwebgui.domain.server.process.log;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class SseServerServerProcessLogsObserver implements ServerProcessLogMessageObserver
{
    private static final ConcurrentLinkedDeque<SseEmitter> serverLogsEmitters = new ConcurrentLinkedDeque<>();
    private static final ScheduledExecutorService SERVER_LOGS_EMITTER_HEALTHCHECK_SERVICE = Executors.newSingleThreadScheduledExecutor();

    @PostConstruct
    public void postConstruct()
    {
        SERVER_LOGS_EMITTER_HEALTHCHECK_SERVICE.scheduleAtFixedRate(this::performServerLogsEmittersHealthcheck, 5, 10, TimeUnit.SECONDS);
    }

    private void performServerLogsEmittersHealthcheck()
    {
        try
        {
            serverLogsEmitters.forEach(emitter ->
            {
                try
                {
                    emitter.send(SseEmitter.event().name("ping").data("healthcheck"));
                }
                catch (IOException ex)
                {
                    log.warn("SseEmitter did not respond to ping event. Connection will be closed.", ex);
                    emitter.complete(); //Not needed... but just in case...
                }
            });
        }
        catch (Exception exception)
        {
            log.warn("Could not perform SseEmitters healthcheck. Reason: {}", exception.getMessage());
        }
    }

    public SseEmitter getServerLogEmitter()
    {
        SseEmitter emitter = new SseEmitter(0L);
        emitter.onTimeout(() ->
        {
            serverLogsEmitters.remove(emitter);
            log.info("SseEmitter timeout");
            emitter.complete();
        });
        emitter.onError(throwable ->
        {
            serverLogsEmitters.remove(emitter);
            log.warn("SseEmitter error", throwable);
            emitter.complete();
        });
        emitter.onCompletion(() -> serverLogsEmitters.remove(emitter));
        serverLogsEmitters.add(emitter);
        return emitter;
    }

    @Override
    public void handleServerLogMessage(String log)
    {
        CompletableFuture.runAsync(new SubmitLogTask(log));
    }

    private record SubmitLogTask(String log) implements Runnable
    {
        @Override
        public void run()
        {
            emitSseLog(log);
        }

        private void emitSseLog(String line)
        {
            serverLogsEmitters.forEach(emitter ->
            {
                try
                {
                    emitter.send(line);
                }
                catch (IOException e)
                {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}
