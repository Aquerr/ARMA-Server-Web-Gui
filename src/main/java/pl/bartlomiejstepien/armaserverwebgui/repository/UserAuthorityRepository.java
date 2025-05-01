package pl.bartlomiejstepien.armaserverwebgui.repository;

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pl.bartlomiejstepien.armaserverwebgui.domain.user.model.AuthorityEntity;
import pl.bartlomiejstepien.armaserverwebgui.domain.user.model.UserAuthorityEntity;

@Repository
@RequiredArgsConstructor
public class UserAuthorityRepository
{

    private final EntityManager entityManager;

    @Transactional
    public void deleteAll()
    {
        entityManager.createQuery("DELETE FROM UserAuthorityEntity").executeUpdate();
    }

    @Transactional
    public Set<AuthorityEntity> findUserAuthorities(int userId)
    {
        return (Set<AuthorityEntity>) entityManager.createQuery("FROM AuthorityEntity authority WHERE authority.id " +
                        "IN (SELECT user_authority.authorityId FROM UserAuthorityEntity user_authority WHERE user_authority.userId = :userId)")
                .setParameter("userId", userId)
                .getResultList().stream()
                .collect(Collectors.toSet());
    }

    @Transactional
    public void saveUserAuthorities(int userId, Set<String> authorities)
    {
        List<AuthorityEntity> authorityEntities = entityManager.createQuery("FROM AuthorityEntity authority_entity " +
                        "WHERE authority_entity.code IN (:authorities)")
                .setParameter("authorities", authorities)
                .getResultList();

        deleteByUserId(userId);

        for (AuthorityEntity authorityEntity : authorityEntities)
        {
            entityManager.persist(UserAuthorityEntity.builder()
                    .userId(userId)
                    .authorityId(authorityEntity.getId())
                    .build());
        }
    }

    @Transactional
    public void deleteByUserId(int userId)
    {
        entityManager.createQuery("DELETE FROM UserAuthorityEntity user_authority_entity WHERE user_authority_entity.userId = :userId")
                .setParameter("userId", userId)
                .executeUpdate();
    }
}
