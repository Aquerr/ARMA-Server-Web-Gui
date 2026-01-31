package pl.bartlomiejstepien.armaserverwebgui.domain.user;

import pl.bartlomiejstepien.armaserverwebgui.domain.user.dto.AswgUser;
import pl.bartlomiejstepien.armaserverwebgui.domain.user.dto.AswgUserWithPassword;

import java.time.OffsetDateTime;

public interface UserService
{
    void deleteUser(int userId);

    void deleteUser(String username);

    void addNewUser(AswgUserWithPassword user);

    void updateUser(AswgUser user);

    void updatePassword(int userId, String password);

    void updateLastSuccessLoginDateTime(int userId, OffsetDateTime loginDateTime);
}
