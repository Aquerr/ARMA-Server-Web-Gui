package pl.bartlomiejstepien.armaserverwebgui.application.security;

import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import pl.bartlomiejstepien.armaserverwebgui.application.auth.AswgAuthentication;
import pl.bartlomiejstepien.armaserverwebgui.domain.user.dto.AswgUser;
import pl.bartlomiejstepien.armaserverwebgui.domain.user.dto.AswgUserDetails;

import java.util.Optional;

@Component
@AllArgsConstructor
public class AuthenticationFacade
{
    public Optional<AswgUserDetails> getCurrentUser()
    {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .filter(authentication -> AswgAuthentication.class.isAssignableFrom(authentication.getClass()))
                .map(AswgAuthentication.class::cast)
                .map(AswgAuthentication::getPrincipal);
    }
}
