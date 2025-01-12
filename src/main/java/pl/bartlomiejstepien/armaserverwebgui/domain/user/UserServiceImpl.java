package pl.bartlomiejstepien.armaserverwebgui.domain.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.bartlomiejstepien.armaserverwebgui.application.config.ASWGConfig;
import pl.bartlomiejstepien.armaserverwebgui.application.security.AswgAuthority;
import pl.bartlomiejstepien.armaserverwebgui.domain.user.dto.AswgUser;
import pl.bartlomiejstepien.armaserverwebgui.domain.user.dto.AswgUserWithPassword;
import pl.bartlomiejstepien.armaserverwebgui.domain.user.exception.UsernameAlreadyExistsException;
import pl.bartlomiejstepien.armaserverwebgui.interfaces.user.model.AswgUserEntity;
import pl.bartlomiejstepien.armaserverwebgui.interfaces.user.model.AuthorityEntity;
import pl.bartlomiejstepien.armaserverwebgui.interfaces.user.repository.UserAuthorityRepository;
import pl.bartlomiejstepien.armaserverwebgui.interfaces.user.repository.UserRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.time.OffsetDateTime;
import java.util.EnumSet;
import java.util.HashSet;
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
    public void onApplicationReady(ApplicationReadyEvent event)
    {
        // Insert default user to db
        // TODO: Later we will implement first login screen in ASWG,
        // TODO: where user will be able to set up such first account.
        userRepository.findByUsername(aswgConfig.getUsername())
                .flatMap(this::resetDefaultAswgUserIfNeeded)
                .switchIfEmpty(Mono.defer(this::insertDefaultAswgUser)
                        .then(Mono.empty()))
                .subscribe();
    }

    @Override
    public Mono<AswgUser> getUser(String username)
    {
        return userRepository.findByUsername(username)
                .zipWhen(this::fetchAuthorities)
                .map(this::toAswgUser);
    }

    @Override
    public Mono<AswgUserWithPassword> getUserWithPassword(String username)
    {
        return userRepository.findByUsername(username)
                .zipWhen(this::fetchAuthorities)
                .map(this::toAswgUserWithPassword);
    }

    @Override
    public Flux<AswgUser> getUsers()
    {
        return userRepository.findAll()
                .flatMap(entity -> fetchAuthorities(entity)
                        .map(authorities -> toAswgUser(Tuples.of(entity, authorities))));
    }

    @Override
    public Mono<Void> deleteUser(int userId)
    {
        return this.userRepository.deleteById(userId)
                .then(this.userAuthorityRepository.deleteByUserId(userId));
    }

    @Override
    public Mono<Void> deleteUser(String username)
    {
        return this.userRepository.findByUsername(username)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Username does not exist")))
                .flatMap(entity -> this.userAuthorityRepository.deleteByUserId(entity.getId())
                        .then(this.userRepository.deleteById(entity.getId())));
    }

    @Override
    public Mono<Void> addNewUser(AswgUserWithPassword user)
    {
        return this.userRepository.findByUsername(user.getUsername())
                .flatMap(entity -> Mono.error(new UsernameAlreadyExistsException("Given username already exists!")))
                .switchIfEmpty(Mono.just(withEncodedPassword(user)))
                .map(t -> withEncodedPassword(user))
                .map(t -> toEntity(user.toBuilder()
                        .createdDate(OffsetDateTime.now())
                        .build()))
                .flatMap(this.userRepository::save)
                .flatMap(entity -> this.userAuthorityRepository.saveUserAuthorities(entity.getId(), user.getAuthorities().stream()
                        .map(AswgAuthority::getCode).collect(Collectors.toSet())))
                .then();
    }

    private AswgUserWithPassword withEncodedPassword(AswgUserWithPassword entity)
    {
        return entity.toBuilder()
                .password(passwordEncoder.encode(entity.getPassword()))
                .build();
    }

    @Override
    public Mono<Void> updateUser(AswgUserWithPassword user)
    {
        if (user.getId() == null)
            return Mono.error(new IllegalArgumentException("No user id to update has been provided!"));

        // Merge with existing
        return this.userRepository.findById(user.getId())
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
                .flatMap(this.userRepository::save)
                .flatMap(entity -> this.userAuthorityRepository.saveUserAuthorities(entity.getId(), user.getAuthorities().stream()
                        .map(AswgAuthority::getCode)
                        .collect(Collectors.toSet())));
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

    private AswgUser toAswgUser(Tuple2<AswgUserEntity, Set<AuthorityEntity>> objects)
    {
        return toDomain(objects.getT1()).toBuilder()
                .authorities(objects.getT2().stream()
                        .map(this::toDomainAuthority)
                        .collect(Collectors.toSet()))
                .build();
    }

    private AswgUserWithPassword toAswgUserWithPassword(Tuple2<AswgUserEntity, Set<AuthorityEntity>> objects)
    {
        return toDomainWithPassword(objects.getT1()).toBuilder()
                .authorities(objects.getT2().stream()
                        .map(this::toDomainAuthority)
                        .collect(Collectors.toSet()))
                .build();
    }

    private AswgAuthority toDomainAuthority(AuthorityEntity authorityEntity)
    {
        return AswgAuthority.findByCode(authorityEntity.getCode())
                .orElseThrow(() -> new RuntimeException("User has an invalid authority: " + authorityEntity.getCode()));
    }

    private Mono<Set<AuthorityEntity>> fetchAuthorities(AswgUserEntity entity)
    {
        return userAuthorityRepository.findUserAuthorities(entity.getId())
                .collectList()
                .map(HashSet::new);
    }

    private Mono<AswgUserEntity> resetDefaultAswgUserIfNeeded(AswgUserEntity entity)
    {
        if (!aswgConfig.isResetDefaultUser())
            return Mono.just(entity);

        log.info("Resetting default user...");
        entity.setUsername(aswgConfig.getUsername());
        entity.setPassword(passwordEncoder.encode(aswgConfig.getPassword()));
        entity.setLocked(false);
        entity.setCreatedDateTime(OffsetDateTime.now());

        return userRepository.save(entity)
                .then(this.userAuthorityRepository.saveUserAuthorities(entity.getId(), EnumSet.allOf(AswgAuthority.class).stream()
                        .map(AswgAuthority::getCode)
                        .collect(Collectors.toSet())))
                .then(Mono.just(entity));
    }

    private Mono<Void> insertDefaultAswgUser()
    {
        log.info("Creating default aswg user: {}", aswgConfig.getUsername());
        AswgUserWithPassword aswgUser = prepareDefaultAswgUser();
        return addNewUser(aswgUser);
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
