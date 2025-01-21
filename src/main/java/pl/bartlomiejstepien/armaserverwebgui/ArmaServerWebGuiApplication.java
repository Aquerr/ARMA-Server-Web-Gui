package pl.bartlomiejstepien.armaserverwebgui;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import pl.bartlomiejstepien.armaserverwebgui.application.config.PropertySourcesConfigurer;

@EnableCaching
@EnableScheduling
@SpringBootApplication
public class ArmaServerWebGuiApplication
{
    public static void main(String[] args)
    {
        SpringApplicationBuilder springApplicationBuilder = new SpringApplicationBuilder();
        springApplicationBuilder.sources(ArmaServerWebGuiApplication.class);
        springApplicationBuilder.listeners(new PropertySourcesConfigurer());
        springApplicationBuilder.run(args);
    }
}
