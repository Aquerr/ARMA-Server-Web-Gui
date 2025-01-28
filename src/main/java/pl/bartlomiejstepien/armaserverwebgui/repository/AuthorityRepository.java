package pl.bartlomiejstepien.armaserverwebgui.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import pl.bartlomiejstepien.armaserverwebgui.domain.user.model.AuthorityEntity;

public interface AuthorityRepository extends ReactiveCrudRepository<AuthorityEntity, Integer>
{

}
