package pl.bartlomiejstepien.armaserverwebgui.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.codec.multipart.DefaultPartHttpMessageReader;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
public class WebConfiguration implements WebFluxConfigurer
{
//    @Override
//    public void configureHttpMessageCodecs(ServerCodecConfigurer configurer)
//    {
//        DefaultPartHttpMessageReader reader = new DefaultPartHttpMessageReader();
//        reader.setMaxParts(10);
//        reader.setEnableLoggingRequestDetails(true);
//    }
}
