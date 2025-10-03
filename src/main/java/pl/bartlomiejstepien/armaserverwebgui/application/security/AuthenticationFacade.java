package pl.bartlomiejstepien.armaserverwebgui.application.security;

import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import pl.bartlomiejstepien.armaserverwebgui.domain.user.dto.AswgUser;

import java.util.Optional;

@Component
@AllArgsConstructor
public class AuthenticationFacade
{
    public Optional<AswgUser> getCurrentUser()
    {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .filter(authentication -> authentication.getPrincipal() instanceof AswgUser)
                .map(authentication -> (AswgUser) authentication.getPrincipal());
    }
}
