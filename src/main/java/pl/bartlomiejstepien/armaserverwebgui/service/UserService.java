package pl.bartlomiejstepien.armaserverwebgui.service;

import pl.bartlomiejstepien.armaserverwebgui.model.UserProfile;

public interface UserService
{
    /**
     * Tries to authenticate user by given credentials and generate jwt token.
     * @param username
     * @param password
     * @return jwt token
     */
    String authenticate(String username, String password);

    UserProfile getUserProfile(String name);
}
