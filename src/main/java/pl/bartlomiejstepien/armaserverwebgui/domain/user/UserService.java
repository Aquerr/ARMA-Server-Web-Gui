package pl.bartlomiejstepien.armaserverwebgui.domain.user;

import pl.bartlomiejstepien.armaserverwebgui.domain.user.dto.UserCreateCommand;
import pl.bartlomiejstepien.armaserverwebgui.domain.user.dto.UserUpdateCommand;

import java.time.OffsetDateTime;

public interface UserService
{
    void deleteUser(int userId);

    void deleteUser(String username);

    void addNewUser(UserCreateCommand createCommand);

    void updateUser(UserUpdateCommand updateCommand);

    void updatePassword(int userId, String password);

    void updateLastSuccessLoginDateTime(int userId, OffsetDateTime loginDateTime);
}
