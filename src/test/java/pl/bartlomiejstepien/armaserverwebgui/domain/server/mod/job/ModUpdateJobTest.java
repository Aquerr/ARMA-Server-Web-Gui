package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.job;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.ModService;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.SteamService;

@ExtendWith(SpringExtension.class)
class ModUpdateJobTest
{
    @MockitoBean
    private ModService modService;
    @MockitoBean
    private SteamService steamService;

    @InjectMocks
    private ModUpdateJob modUpdateJob;

    @Test
    void shouldNotUpdateModsWhenJobIsDisabled()
    {
        // given

        // when
        modUpdateJob.updateMods();

        // then

    }
}