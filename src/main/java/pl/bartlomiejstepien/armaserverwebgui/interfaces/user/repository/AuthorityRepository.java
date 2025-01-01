package pl.bartlomiejstepien.armaserverwebgui.interfaces.user.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import pl.bartlomiejstepien.armaserverwebgui.interfaces.user.model.AuthorityEntity;

public interface AuthorityRepository extends ReactiveCrudRepository<AuthorityEntity, Integer>
{

}
