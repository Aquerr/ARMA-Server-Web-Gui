package pl.bartlomiejstepien.armaserverwebgui.application.auth;

import java.util.Set;
import pl.bartlomiejstepien.armaserverwebgui.application.security.AswgAuthority;

public record JwtToken(String jwt, Set<AswgAuthority> authorities)
{

}
