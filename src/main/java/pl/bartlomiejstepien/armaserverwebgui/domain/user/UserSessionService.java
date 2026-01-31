package pl.bartlomiejstepien.armaserverwebgui.domain.user;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.springframework.stereotype.Service;
import pl.bartlomiejstepien.armaserverwebgui.domain.user.dto.AswgUserDetails;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
public class UserSessionService
{
    private final UserLoaderService userLoaderService;
    private final LoadingCache<String, AswgUserDetails> userSessionCache;

    public UserSessionService(UserLoaderService userLoaderService)
    {
        this.userLoaderService = userLoaderService;
        this.userSessionCache = Caffeine.newBuilder()
                .expireAfterWrite(Duration.of(30, ChronoUnit.MINUTES))
                .maximumSize(50)
                .build((username) -> Optional.ofNullable(this.userLoaderService.getUser(username))
                .map(user -> AswgUserDetails.builder()
                        .username(user.getUsername())
                        .authorities(user.getAuthorities())
                        .build())
                .orElse(null));
    }

    public Optional<AswgUserDetails> getUserSession(String username)
    {
        return Optional.ofNullable(this.userSessionCache.get(username));
    }

    public void evict(String username)
    {
        this.userSessionCache.invalidate(username);
    }
}
