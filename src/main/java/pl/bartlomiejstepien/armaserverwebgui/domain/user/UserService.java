package pl.bartlomiejstepien.armaserverwebgui.domain.user;

import pl.bartlomiejstepien.armaserverwebgui.domain.user.dto.AswgUser;
import pl.bartlomiejstepien.armaserverwebgui.domain.user.dto.AswgUserWithPassword;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserService
{
    Mono<AswgUser> getUser(String username);

    Mono<AswgUserWithPassword> getUserWithPassword(String username);

    Flux<AswgUser> getUsers();

    Mono<Void> deleteUser(int userId);

    Mono<Void> deleteUser(String username);

    Mono<Void> addNewUser(AswgUserWithPassword user);

    Mono<Void> updateUser(AswgUserWithPassword user);
}
