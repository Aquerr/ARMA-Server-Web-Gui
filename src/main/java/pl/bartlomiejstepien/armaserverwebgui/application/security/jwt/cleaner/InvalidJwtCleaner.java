package pl.bartlomiejstepien.armaserverwebgui.application.security.jwt.cleaner;

import java.util.concurrent.TimeUnit;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.bartlomiejstepien.armaserverwebgui.application.security.jwt.InvalidJwtTokenRepository;

@Component
@AllArgsConstructor
@Slf4j
public class InvalidJwtCleaner
{
    private final InvalidJwtTokenRepository invalidJwtTokenRepository;

    @Scheduled(fixedRate = 1L, timeUnit = TimeUnit.HOURS)
    @Transactional
    public void cleanInvalidJwts()
    {
        log.info("Deleting old invalid JWT tokens from database.");
        invalidJwtTokenRepository.deleteAllByExpirationDateTimeBeforeNow();
    }
}