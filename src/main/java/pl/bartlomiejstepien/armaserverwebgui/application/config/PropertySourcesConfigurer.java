package pl.bartlomiejstepien.armaserverwebgui.application.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.io.support.ResourcePropertySource;

@Slf4j
public class PropertySourcesConfigurer implements ApplicationListener<ApplicationEnvironmentPreparedEvent>
{
    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event)
    {
        ConfigurableEnvironment environment = event.getEnvironment();

        MutablePropertySources propertySources = environment.getPropertySources();

        // Lower priority
        registerRequiredConfig(propertySources);

        // Higher priority
        registerOptionalConfig(propertySources);
    }

    private void registerOptionalConfig(MutablePropertySources propertySources)
    {
        try
        {
            propertySources.addFirst(new ResourcePropertySource("file:aswg-config.properties"));
        }
        catch (Exception exception)
        {
            log.info("aswg-config.properties file not found.");
        }
    }

    private void registerRequiredConfig(MutablePropertySources propertySources)
    {
        try
        {
            propertySources.addLast(new ResourcePropertySource("classpath:aswg-default-config.properties"));
        }
        catch (Exception exception)
        {
            log.error("aswg-default-config.properties file not found.");
        }
    }
}
