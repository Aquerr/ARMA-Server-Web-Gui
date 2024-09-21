package pl.bartlomiejstepien.armaserverwebgui.domain.server.process;

import pl.bartlomiejstepien.armaserverwebgui.domain.server.process.model.ArmaServerParameters;
import reactor.core.publisher.Mono;

public interface ArmaServerParametersGenerator
{
    Mono<ArmaServerParameters> generateParameters();
}
