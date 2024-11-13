package pl.bartlomiejstepien.armaserverwebgui;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.context.SpringBootContextLoader;
import pl.bartlomiejstepien.armaserverwebgui.application.config.PropertySourcesConfigurer;

public class TestSpringContextLoader extends SpringBootContextLoader
{
    @Override
    protected SpringApplication getSpringApplication()
    {
        return new SpringApplicationBuilder()
                .listeners(new PropertySourcesConfigurer())
                .build();
    }
}
