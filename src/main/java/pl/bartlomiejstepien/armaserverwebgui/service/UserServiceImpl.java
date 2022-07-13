package pl.bartlomiejstepien.armaserverwebgui.service;

import lombok.AllArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import pl.bartlomiejstepien.armaserverwebgui.config.ASWGConfig;
import pl.bartlomiejstepien.armaserverwebgui.config.security.JwtService;
import pl.bartlomiejstepien.armaserverwebgui.model.AppUser;
import pl.bartlomiejstepien.armaserverwebgui.model.UserProfile;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService
{
    private final ASWGConfig aswgConfig;
    private final JwtService jwtService;

    @Override
    public String authenticate(String username, String password)
    {
        AppUser user = getUsers().stream().filter(appUser -> appUser.getUsername().equals(username))
                .findFirst()
                .filter(appUser -> appUser.getPassword().equals(password))
                .orElse(null);
        if (user == null)
            throw new BadCredentialsException("Provided credentials does not match any account!");

        return jwtService.createJwt(username);
    }

    @Override
    public UserProfile getUserProfile(String name)
    {
        return getUsers().stream()
                .filter(appUser -> appUser.getUsername().equals(name))
                .map(appUser -> UserProfile.builder()
                        .username(appUser.getUsername())
                        .roles(appUser.getRoles())
                        .createDate(appUser.getCreatedDate())
                        .build())
                .findFirst()
                .orElse(null);
    }

    private List<AppUser> getUsers()
    {
        return List.of(
                new AppUser(this.aswgConfig.getUsername(), this.aswgConfig.getPassword(), List.of("USER"), OffsetDateTime.now())
        );
    }
}
