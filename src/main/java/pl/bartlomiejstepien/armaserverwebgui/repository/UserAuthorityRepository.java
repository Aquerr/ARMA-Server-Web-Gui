package pl.bartlomiejstepien.armaserverwebgui.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;
import pl.bartlomiejstepien.armaserverwebgui.domain.user.model.AuthorityEntity;
import pl.bartlomiejstepien.armaserverwebgui.domain.user.model.UserAuthorityEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

@Repository
@RequiredArgsConstructor
public class UserAuthorityRepository {

    private final R2dbcEntityTemplate r2dbcEntityTemplate;

    public Mono<Void> deleteAll()
    {
        return r2dbcEntityTemplate.delete(UserAuthorityEntity.class)
                .all()
                .then();
    }

    public Flux<AuthorityEntity> findUserAuthorities(int userId)
    {
        return r2dbcEntityTemplate.select(UserAuthorityEntity.class)
                .matching(Query.query(Criteria.where("user_id").is(userId)))
                .all()
                .map(UserAuthorityEntity::getAuthorityId)
                .collectList()
                .flatMapMany(authorityIds -> this.r2dbcEntityTemplate
                        .select(AuthorityEntity.class)
                        .matching(Query.query(Criteria.where("id").in(authorityIds))).all());
    }

    public Mono<Void> saveUserAuthorities(int userId, Set<String> authorities)
    {
        return r2dbcEntityTemplate.select(AuthorityEntity.class)
                .matching(Query.query(Criteria.where("code").in(authorities)))
                .all()
                .map(AuthorityEntity::getId)
                .collectList()
                .flatMap(authorityIds -> r2dbcEntityTemplate.delete(UserAuthorityEntity.class)
                        .matching(Query.query(Criteria.where("user_id").is(userId)))
                        .all()
                        .then(Mono.just(authorityIds)))
                .map(authorityIds -> authorityIds.stream()
                        .map(authorityId -> UserAuthorityEntity.builder()
                                .userId(userId)
                                .authorityId(authorityId)
                                .build())
                        .toList())
                .flatMapMany(Flux::fromIterable)
                .flatMap(userAuthorityEntity -> r2dbcEntityTemplate.insert(UserAuthorityEntity.class)
                        .using(userAuthorityEntity))
                .collectList()
                .then();
    }

    public Mono<Void> deleteByUserId(int userId)
    {
        return r2dbcEntityTemplate.delete(UserAuthorityEntity.class)
                .matching(Query.query(Criteria.where("user_id").is(userId)))
                .all()
                .then();
    }
}
