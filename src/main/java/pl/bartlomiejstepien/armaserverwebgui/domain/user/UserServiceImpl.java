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
import pl.bartlomiejstepien.armaserverwebgui.repository.UserAuthorityRepository;
import pl.bartlomiejstepien.armaserverwebgui.repository.UserRepository;

import java.time.OffsetDateTime;
import java.util.EnumSet;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService
{
    private final UserSessionService userSessionService;
    private final PasswordEncoder passwordEncoder;
    private final UserLoaderService userLoaderService;
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

    @Transactional
    @Override
    public void deleteUser(int userId)
    {
        String username = this.userLoaderService.getUser(userId).getUsername();
        this.userRepository.deleteById(userId);
        this.userAuthorityRepository.deleteByUserId(userId);
        this.userSessionService.evict(username);
    }

    @Transactional
    @Override
    public void deleteUser(String username)
    {
        AswgUserEntity userEntity = this.userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Username does not exist"));
        userAuthorityRepository.deleteByUserId(userEntity.getId());
        userRepository.deleteById(userEntity.getId());
        this.userSessionService.evict(username);
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
    public void updateUser(AswgUser user)
    {
        if (user.getId() == null)
            throw new IllegalArgumentException("No user id to update has been provided!");

        // Merge with existing
        this.userRepository.findById(user.getId())
                .map(entity ->
                {
                    AswgUserEntity entityToUpdate = new AswgUserEntity();
                    entityToUpdate.setId(entity.getId());
                    entityToUpdate.setUsername(entity.getUsername());
                    entityToUpdate.setLocked(user.isLocked());
                    entityToUpdate.setCreatedDateTime(entity.getCreatedDateTime());
                    entityToUpdate.setLastSuccessLoginDateTime(entity.getLastSuccessLoginDateTime());
                    return entityToUpdate;
                })
                .map(this.userRepository::save)
                .ifPresent(entity -> this.userAuthorityRepository.saveUserAuthorities(entity.getId(), user.getAuthorities().stream()
                        .map(AswgAuthority::getCode)
                        .collect(Collectors.toSet())));
        this.userSessionService.evict(user.getUsername());
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
        this.userSessionService.evict(userEntity.getUsername());
    }

    @Override
    @Transactional
    public void updateLastSuccessLoginDateTime(int userId, OffsetDateTime loginDateTime)
    {
        this.userRepository.updateLastSuccessLoginDateTime(userId, loginDateTime);
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

    private void resetDefaultAswgUserIfNeeded(AswgUserEntity entity)
    {
        // Authorities reset
        if (aswgConfig.isResetDefaultUser())
        {
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

        // Reset default authorities always. The main account should always have all possible authorities.
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
}
