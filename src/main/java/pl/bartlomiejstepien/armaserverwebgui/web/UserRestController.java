package pl.bartlomiejstepien.armaserverwebgui.web;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.bartlomiejstepien.armaserverwebgui.application.security.AswgAuthority;
import pl.bartlomiejstepien.armaserverwebgui.application.security.authorize.annotation.HasPermissionUsersAdd;
import pl.bartlomiejstepien.armaserverwebgui.application.security.authorize.annotation.HasPermissionUsersDelete;
import pl.bartlomiejstepien.armaserverwebgui.application.security.authorize.annotation.HasPermissionUsersUpdate;
import pl.bartlomiejstepien.armaserverwebgui.application.security.authorize.annotation.HasPermissionUsersView;
import pl.bartlomiejstepien.armaserverwebgui.domain.user.UserService;
import pl.bartlomiejstepien.armaserverwebgui.domain.user.dto.AswgUser;
import pl.bartlomiejstepien.armaserverwebgui.domain.user.dto.AswgUserWithPassword;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserRestController
{
    private final UserService userService;

    @HasPermissionUsersView
    @GetMapping
    public List<AswgUser> getUsers()
    {
        return userService.getUsers();
    }

    @HasPermissionUsersAdd
    @PostMapping
    public void addUser(@RequestBody NewUserRequest userRequest)
    {
        this.userService.addNewUser(AswgUserWithPassword.builder()
                .username(userRequest.getUsername())
                .password(userRequest.getPassword())
                .authorities(userRequest.getAuthorities().stream()
                        .map(AswgAuthority::findByCode)
                        .map(Optional::orElseThrow)
                        .collect(Collectors.toSet()))
                .build());
    }

    @HasPermissionUsersUpdate
    @PutMapping("/{id}")
    public void updateUser(@PathVariable("id") int userId,
                                 @RequestBody UpdateUserRequest updateUserRequest)
    {
        this.userService.updateUser(AswgUserWithPassword.builder()
                .id(userId)
                .password(updateUserRequest.getPassword())
                .locked(updateUserRequest.isLocked())
                .authorities(updateUserRequest.getAuthorities().stream()
                        .map(AswgAuthority::findByCode)
                        .map(Optional::orElseThrow)
                        .collect(Collectors.toSet()))
                .build());
    }

    @HasPermissionUsersDelete
    @DeleteMapping("/{id}")
    public void deleteUser(Authentication authentication,
                                 @PathVariable("id") int userId)
    {
        if (authentication.getPrincipal() instanceof AswgUser aswgUser && aswgUser.getId() == userId) {
                throw new IllegalArgumentException("Cannot delete self.");
        }

        this.userService.deleteUser(userId);
    }

    @HasPermissionUsersUpdate
    @PostMapping("/{id}/password-change")
    public void updateUserPassword(@PathVariable("id") int userId,
                                   @RequestBody PasswordChange passwordChange)
    {
        this.userService.updatePassword(userId, passwordChange.getPassword());
    }

    @Data
    public static class PasswordChange
    {
        private String password;
    }

    @Data
    public static class NewUserRequest
    {
        private String username;
        private String password;
        private Set<String> authorities;
    }

    @Data
    public static class UpdateUserRequest
    {
        private String password;
        private Set<String> authorities;
        private boolean locked;
    }
}
