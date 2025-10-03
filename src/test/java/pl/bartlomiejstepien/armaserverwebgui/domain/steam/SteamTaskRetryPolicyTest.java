package pl.bartlomiejstepien.armaserverwebgui.domain.steam;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.QueuedSteamTask;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.WorkshopModInstallSteamTask;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class SteamTaskRetryPolicyTest
{
    private static final int FILE_ID = 1;
    private static final String FILE_TITLE = "FILE_TITLE";
    private static final String ISSUER = "issuer_user";

    @InjectMocks
    private SteamTaskRetryPolicy retryPolicy;

    @ParameterizedTest
    @CsvSource(
            {"0, true",
                    "1, true",
                    "2, false",
                    "3, false",
                    "4, false"}
    )
    void shouldReturnIfCanRetry(int currentAttemptCount, boolean expected)
    {
        QueuedSteamTask queuedSteamTask = new QueuedSteamTask(UUID.randomUUID(), new WorkshopModInstallSteamTask(FILE_ID, FILE_TITLE, false, ISSUER), currentAttemptCount);

        boolean canRetry = retryPolicy.canRetry(queuedSteamTask);

        assertThat(canRetry).isEqualTo(expected);
    }
}