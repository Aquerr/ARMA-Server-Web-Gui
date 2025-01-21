package pl.bartlomiejstepien.armaserverwebgui.application.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration(proxyBeanMethods = false)
public class CacheConfig
{
    @Bean
    public CacheManager aswgCacheManager()
    {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(prepareCaffeine(Duration.ofMinutes(30), 30));
        cacheManager.registerCustomCache("workshop-query", prepareCaffeine(Duration.ofMinutes(30), 15).buildAsync());
        cacheManager.registerCustomCache("workshop-get-mod", prepareCaffeine(Duration.ofMinutes(30), 50).buildAsync());
        return cacheManager;
    }

    private Caffeine<Object, Object> prepareCaffeine(Duration expirationTime, int size) {
        return Caffeine.newBuilder()
                .maximumSize(size)
                .expireAfterWrite(expirationTime);
    }
}
