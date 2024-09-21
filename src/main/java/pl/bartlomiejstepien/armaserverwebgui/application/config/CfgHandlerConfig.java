package pl.bartlomiejstepien.armaserverwebgui.application.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.CfgFileHandler;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.DefaultCfgConfigReader;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.util.cfg.DefaultCfgConfigWriter;

@Configuration(proxyBeanMethods = false)
public class CfgHandlerConfig
{
    @Bean
    public CfgFileHandler cfgFileHandler()
    {
        return new CfgFileHandler(DefaultCfgConfigReader.INSTNACE, DefaultCfgConfigWriter.INSTANCE);
    }
}
