package pl.bartlomiejstepien.armaserverwebgui.domain.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.bartlomiejstepien.armaserverwebgui.application.config.ASWGConfig;
import pl.bartlomiejstepien.armaserverwebgui.application.security.AswgAuthority;
import pl.bartlomiejstepien.armaserverwebgui.domain.user.dto.AswgUser;
import pl.bartlomiejstepien.armaserverwebgui.domain.user.dto.AswgUserWithPassword;
import pl.bartlomiejstepien.armaserverwebgui.domain.user.exception.UsernameAlreadyExistsException;
import pl.bartlomiejstepien.armaserverwebgui.domain.user.model.AswgUserEntity;
import pl.bartlomiejstepien.armaserverwebgui.domain.user.model.AuthorityEntity;
import pl.bartlomiejstepien.armaserverwebgui.repository.UserAuthorityRepository;
import pl.bartlomiejstepien.armaserverwebgui.repository.UserRepository;

import java.time.OffsetDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService
{
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserAuthorityRepository userAuthorityRepository;
    private final ASWGConfig aswgConfig;

    @EventListener
    @Transactional
    public void onApplicationReady(ApplicationReadyEvent event)
    {
        // Insert default user to db
        // TODO: Later we will implement first login screen in ASWG,
        // TODO: where user will be able to set up such first account.

        AswgUserEntity userEntity = userRepository.findByUsername(aswgConfig.getUsername()).orElse(null);

        if (userEntity == null)
        {
            insertDefaultAswgUser();
        }
        else
        {
            resetDefaultAswgUserIfNeeded(userEntity);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public AswgUser getUser(String username)
    {
        AswgUserEntity userEntity = userRepository.findByUsername(username).orElse(null);
        if (userEntity == null)
            return null;
        Set<AuthorityEntity> authorityEntities = fetchAuthorities(userEntity);
        return toAswgUser(userEntity, authorityEntities);
    }

    @Transactional(readOnly = true)
    @Override
    public AswgUserWithPassword getUserWithPassword(String username)
    {
        AswgUserEntity userEntity = userRepository.findByUsername(username).orElse(null);
        if (userEntity == null)
            return null;
        Set<AuthorityEntity> authorityEntities = fetchAuthorities(userEntity);
        return toAswgUserWithPassword(userEntity, authorityEntities);
    }

    @Transactional(readOnly = true)
    @Override
    public List<AswgUser> getUsers()
    {
        return userRepository.findAll().stream()
                .map(entity -> toAswgUser(entity, fetchAuthorities(entity)))
                .toList();
    }

    @Transactional
    @Override
    public void deleteUser(int userId)
    {
        this.userRepository.deleteById(userId);
        this.userAuthorityRepository.deleteByUserId(userId);
    }

    @Transactional
    @Override
    public void deleteUser(String username)
    {
        AswgUserEntity userEntity = this.userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Username does not exist"));
        userAuthorityRepository.deleteByUserId(userEntity.getId());
        userRepository.deleteById(userEntity.getId());
    }

    @Transactional
    @Override
    public void addNewUser(AswgUserWithPassword user)
    {
        AswgUserEntity userEntity = this.userRepository.findByUsername(user.getUsername()).orElse(null);
        if (userEntity != null)
            throw new UsernameAlreadyExistsException("Given username already exists!");

        AswgUserEntity newUserEntity = toEntity(withEncodedPassword(user).toBuilder()
                .createdDate(OffsetDateTime.now())
                .build());
        newUserEntity = userRepository.save(newUserEntity);
        userAuthorityRepository.saveUserAuthorities(newUserEntity.getId(), user.getAuthorities().stream()
                .map(AswgAuthority::getCode).collect(Collectors.toSet()));
    }

    private AswgUserWithPassword withEncodedPassword(AswgUserWithPassword entity)
    {
        return entity.toBuilder()
                .password(passwordEncoder.encode(entity.getPassword()))
                .build();
    }

    @Transactional
    @Override
    public void updateUser(AswgUserWithPassword user)
    {
        if (user.getId() == null)
            throw new IllegalArgumentException("No user id to update has been provided!");

        // Merge with existing
        this.userRepository.findById(user.getId())
                .map(entity -> {
                    AswgUserEntity entityToUpdate = new AswgUserEntity();
                    entityToUpdate.setId(entity.getId());
                    entityToUpdate.setUsername(entity.getUsername());
                    entityToUpdate.setPassword(Optional.ofNullable(user.getPassword())
                            .map(passwordEncoder::encode)
                            .orElse(entity.getPassword()));
                    entityToUpdate.setLocked(user.isLocked());
                    entityToUpdate.setCreatedDateTime(entity.getCreatedDateTime());
                    return entityToUpdate;
                })
                .map(this.userRepository::save)
                .ifPresent(entity -> this.userAuthorityRepository.saveUserAuthorities(entity.getId(), user.getAuthorities().stream()
                        .map(AswgAuthority::getCode)
                        .collect(Collectors.toSet())));
    }

    @Override
    @Transactional
    public void updatePassword(int userId, String password)
    {
        AswgUserEntity userEntity = this.userRepository.findById(userId).orElse(null);
        if (userEntity == null)
            throw new IllegalArgumentException("User does not exist for id" + userId);

        userEntity.setPassword(passwordEncoder.encode(password));
        this.userRepository.save(userEntity);
    }

    private AswgUserEntity toEntity(AswgUserWithPassword user)
    {
        AswgUserEntity entity = new AswgUserEntity();
        entity.setId(user.getId());
        entity.setUsername(user.getUsername());
        entity.setPassword(user.getPassword());
        entity.setLocked(user.isLocked());
        entity.setCreatedDateTime(user.getCreatedDate());
        return entity;
    }

    private AswgUserWithPassword prepareDefaultAswgUser()
    {
        return AswgUserWithPassword.builder()
                .username(this.aswgConfig.getUsername())
                .password(this.aswgConfig.getPassword())
                .createdDate(OffsetDateTime.now())
                .authorities(EnumSet.allOf(AswgAuthority.class))
                .build();
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

    private void resetDefaultAswgUserIfNeeded(AswgUserEntity entity)
    {
        if (!aswgConfig.isResetDefaultUser())
            return;

        log.info("Resetting default user: {}", aswgConfig.getUsername());
        entity.setUsername(aswgConfig.getUsername());
        entity.setPassword(passwordEncoder.encode(aswgConfig.getPassword()));
        entity.setLocked(false);
        entity.setCreatedDateTime(OffsetDateTime.now());

        entity = userRepository.save(entity);
        userAuthorityRepository.saveUserAuthorities(entity.getId(), EnumSet.allOf(AswgAuthority.class).stream()
                .map(AswgAuthority::getCode)
                .collect(Collectors.toSet()));
    }

    private void insertDefaultAswgUser()
    {
        log.info("Creating default aswg user: {}", aswgConfig.getUsername());
        AswgUserWithPassword aswgUser = prepareDefaultAswgUser();
        addNewUser(aswgUser);
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
                .build();
    }
}
