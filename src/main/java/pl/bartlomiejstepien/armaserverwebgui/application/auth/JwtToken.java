package pl.bartlomiejstepien.armaserverwebgui.application.auth;

import pl.bartlomiejstepien.armaserverwebgui.application.security.AswgAuthority;

import java.util.Set;

public record JwtToken(String jwt, Set<AswgAuthority> authorities)
{

}
