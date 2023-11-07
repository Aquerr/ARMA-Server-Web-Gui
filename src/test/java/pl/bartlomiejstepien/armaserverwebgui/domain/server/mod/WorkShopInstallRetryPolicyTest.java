package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.WorkshopModInstallationRequest;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class WorkShopInstallRetryPolicyTest
{
    private static final int FILE_ID = 1;
    private static final String FILE_TITLE = "FILE_TITLE";

    @InjectMocks
    private WorkShopInstallRetryPolicy retryPolicy;

    @ParameterizedTest
    @CsvSource(
            {"0, true",
            "1, true",
            "2, true",
            "3, false",
            "4, false"}
    )
    void shouldReturnIfCanRetry(int currentAttemptCount, boolean expected)
    {
        WorkshopModInstallationRequest request = new WorkshopModInstallationRequest(FILE_ID, FILE_TITLE, currentAttemptCount);

        boolean canRetry = retryPolicy.canRetry(request);

        assertThat(canRetry).isEqualTo(expected);
    }
}