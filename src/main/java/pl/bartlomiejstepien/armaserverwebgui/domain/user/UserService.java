package pl.bartlomiejstepien.armaserverwebgui.domain.user;

import pl.bartlomiejstepien.armaserverwebgui.application.model.UserProfile;

public interface UserService
{
    /**
     * Tries to authenticate user by given credentials and generate jwt token.
     * @param username
     * @param password
     * @param ipAddress
     * @return jwt token
     */
    String authenticate(String username, String password, String ipAddress);

    UserProfile getUserProfile(String name);
}
