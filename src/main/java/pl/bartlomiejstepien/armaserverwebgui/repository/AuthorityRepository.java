package pl.bartlomiejstepien.armaserverwebgui.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.bartlomiejstepien.armaserverwebgui.domain.user.model.AuthorityEntity;

public interface AuthorityRepository extends JpaRepository<AuthorityEntity, Integer>
{

}
