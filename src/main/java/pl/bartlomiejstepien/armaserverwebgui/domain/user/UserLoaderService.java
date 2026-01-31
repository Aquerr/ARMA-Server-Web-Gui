package pl.bartlomiejstepien.armaserverwebgui.domain.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.bartlomiejstepien.armaserverwebgui.application.security.AswgAuthority;
import pl.bartlomiejstepien.armaserverwebgui.domain.user.dto.AswgUser;
import pl.bartlomiejstepien.armaserverwebgui.domain.user.dto.AswgUserWithPassword;
import pl.bartlomiejstepien.armaserverwebgui.domain.user.model.AswgUserEntity;
import pl.bartlomiejstepien.armaserverwebgui.domain.user.model.AuthorityEntity;
import pl.bartlomiejstepien.armaserverwebgui.repository.UserAuthorityRepository;
import pl.bartlomiejstepien.armaserverwebgui.repository.UserRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserLoaderService
{
    private final UserRepository userRepository;
    private final UserAuthorityRepository userAuthorityRepository;

    @Transactional(readOnly = true)
    public AswgUser getUser(int userId)
    {
        AswgUserEntity userEntity = userRepository.findById(userId).orElse(null);
        if (userEntity == null)
            return null;
        Set<AuthorityEntity> authorityEntities = fetchAuthorities(userEntity);
        return toAswgUser(userEntity, authorityEntities);
    }

    @Transactional(readOnly = true)
    public AswgUser getUser(String username)
    {
        AswgUserEntity userEntity = userRepository.findByUsername(username).orElse(null);
        if (userEntity == null)
            return null;
        Set<AuthorityEntity> authorityEntities = fetchAuthorities(userEntity);
        return toAswgUser(userEntity, authorityEntities);
    }

    @Transactional(readOnly = true)
    public AswgUserWithPassword getUserWithPassword(String username)
    {
        AswgUserEntity userEntity = userRepository.findByUsername(username).orElse(null);
        if (userEntity == null)
            return null;
        Set<AuthorityEntity> authorityEntities = fetchAuthorities(userEntity);
        return toAswgUserWithPassword(userEntity, authorityEntities);
    }

    @Transactional(readOnly = true)
    public List<AswgUser> getUsers()
    {
        return userRepository.findAll().stream()
                .map(entity -> toAswgUser(entity, fetchAuthorities(entity)))
                .toList();
    }

    private AswgUser toAswgUser(AswgUserEntity userEntity, Set<AuthorityEntity> authorityEntities)
    {
        return toDomain(userEntity).toBuilder()
                .authorities(authorityEntities.stream()
                        .map(this::toDomainAuthority)
                        .collect(Collectors.toSet()))
                .build();
    }

    private AswgUserWithPassword toAswgUserWithPassword(AswgUserEntity userEntity, Set<AuthorityEntity> authorityEntities)
    {
        return toDomainWithPassword(userEntity).toBuilder()
                .authorities(authorityEntities.stream()
                        .map(this::toDomainAuthority)
                        .collect(Collectors.toSet()))
                .build();
    }

    private AswgAuthority toDomainAuthority(AuthorityEntity authorityEntity)
    {
        return AswgAuthority.findByCode(authorityEntity.getCode())
                .orElseThrow(() -> new RuntimeException("User has an invalid authority: " + authorityEntity.getCode()));
    }

    private Set<AuthorityEntity> fetchAuthorities(AswgUserEntity entity)
    {
        return userAuthorityRepository.findUserAuthorities(entity.getId());
    }

    private AswgUserWithPassword toDomainWithPassword(AswgUserEntity entity)
    {
        return AswgUserWithPassword.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .password(entity.getPassword())
                .createdDate(entity.getCreatedDateTime())
                .locked(entity.isLocked())
                .build();
    }

    private AswgUser toDomain(AswgUserEntity entity)
    {
        return AswgUserWithPassword.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .createdDate(entity.getCreatedDateTime())
                .locked(entity.isLocked())
                .lastLoginDate(entity.getLastSuccessLoginDateTime())
                .build();
    }
}
