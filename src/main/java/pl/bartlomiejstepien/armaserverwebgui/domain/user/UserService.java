package pl.bartlomiejstepien.armaserverwebgui.domain.user;

import pl.bartlomiejstepien.armaserverwebgui.domain.user.dto.AswgUser;
import pl.bartlomiejstepien.armaserverwebgui.domain.user.dto.AswgUserWithPassword;

import java.util.List;

public interface UserService
{
    AswgUser getUser(String username);

    AswgUserWithPassword getUserWithPassword(String username);

    List<AswgUser> getUsers();

    void deleteUser(int userId);

    void deleteUser(String username);

    void addNewUser(AswgUserWithPassword user);

    void updateUser(AswgUserWithPassword user);

    void updatePassword(int userId, String password);
}
