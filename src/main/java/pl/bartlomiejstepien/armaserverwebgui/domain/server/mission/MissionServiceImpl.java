package pl.bartlomiejstepien.armaserverwebgui.domain.server.mission;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mission.converter.MissionConverter;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mission.dto.Mission;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mission.dto.Missions;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mission.exception.MissionDoesNotExistException;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mission.exception.MissionFileAlreadyExistsException;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mission.model.MissionEntity;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.model.ArmaServerConfig;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.mission.MissionFileNameHelper;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.mission.MissionFileStorage;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.config.ServerConfigStorage;
import pl.bartlomiejstepien.armaserverwebgui.interfaces.repository.MissionRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class MissionServiceImpl implements MissionService
{
    private final MissionFileStorage missionFileStorage;
    private final MissionRepository missionRepository;
    private final ServerConfigStorage serverConfigStorage;
    private final MissionConverter missionConverter;
    private final MissionFileNameHelper missionFileNameHelper;

    @Override
    public Mono<Void> save(FilePart multipartFile)
    {
        if (missionFileStorage.doesMissionExists(multipartFile.filename()))
            throw new MissionFileAlreadyExistsException();

        try
        {
            String missionTemplate = missionFileNameHelper.resolveMissionNameFromFilePart(multipartFile);
            return missionFileStorage.save(multipartFile)
                    .then(addMission(missionTemplate, missionTemplate));
        }
        catch (IOException exception)
        {
            throw new RuntimeException(exception);
        }
    }

    @Scheduled(fixedDelay = 10, timeUnit = TimeUnit.MINUTES)
    public void missionScan()
    {
        log.info("Scanning for new file missions...");
        List<String> installedMissionTemplates = this.missionFileStorage.getInstalledMissionTemplates();

        Mono.zip(
                Mono.just(installedMissionTemplates),
                this.missionRepository.findAll().collectList()
        )
        .map(this::findNotInstalledTemplates)
        .flatMap(notInstalledTemplates -> notInstalledTemplates.isEmpty() ? Mono.empty() : Mono.just(notInstalledTemplates))
        .doOnNext(notInstalledTemplates -> log.info("Installing new missions: {}", notInstalledTemplates))
        .flatMapMany(Flux::fromIterable)
        .flatMap(template -> addMission(template, template))
        .subscribeOn(Schedulers.boundedElastic())
        .subscribe();
    }

    private List<String> findNotInstalledTemplates(Tuple2<List<String>, List<MissionEntity>> tuple2)
    {
        List<String> installedTemplates = tuple2.getT1();
        List<String> templatesInDB = tuple2.getT2().stream().map(MissionEntity::getTemplate).toList();

        return installedTemplates.stream()
                .filter(template -> !templatesInDB.contains(template))
                .toList();
    }

    @Override
    public Mono<Boolean> deleteMission(String template)
    {
        return this.missionRepository.findByTemplate(template)
                .switchIfEmpty(Mono.error(() -> new MissionDoesNotExistException("No mission exist for template: " + template)))
                .collectList()
                .flatMap(t -> this.missionRepository.deleteByTemplate(template))
                .then(Mono.fromCallable(() -> this.missionFileStorage.deleteMission(template)));
    }

    @Override
    public Mono<Void> saveEnabledMissionList(List<Mission> missions)
    {
        return this.missionRepository.disableAll()
                .collectList()
                .flatMap(v -> Mono.just(missions))
                .flatMap(missionList -> Mono.just(missionList)
                        .filter(missionList1 -> !missionList1.isEmpty())
                        .flatMapMany(missionList2 -> this.missionRepository.updateAllByTemplateSetEnabled(missionList2.stream()
                                .map(Mission::getTemplate)
                                .toList()))
                        .collectList()
                )
                .then(syncConfigMissions());
    }

    @Override
    public Mono<Missions> getMissions()
    {
        return this.missionRepository.findAll()
                .map(this.missionConverter::convertToDomainMission)
                .collectList()
                .map(missionsList ->
                {
                    Map<Boolean, List<Mission>> groupedMissions = missionsList.stream().collect(Collectors.groupingBy(Mission::isEnabled));
                    Missions missions = new Missions();
                    missions.setDisabledMissions(groupedMissions.getOrDefault(false, Collections.emptyList()));
                    missions.setEnabledMissions(groupedMissions.getOrDefault(true, Collections.emptyList()));
                    return missions;
                });
    }

    @Override
    public Mono<Void> addMission(String name, String template)
    {
        Mission mission = Mission.builder()
                .name(name)
                .template(template)
                .difficulty(Mission.Difficulty.REGULAR)
                .enabled(false)
                .parameters(Collections.emptySet())
                .build();

        return this.missionRepository.save(missionConverter.convertToEntity(mission))
                .then();
    }

    @Override
    public Mono<Void> updateMission(long id, Mission mission)
    {
        return this.missionRepository.findById(id)
                .switchIfEmpty(Mono.error(new MissionDoesNotExistException("Mission not found for id = " + id)))
                .map(entity -> this.missionConverter.convertToEntity(mission))
                .flatMap(this.missionRepository::save)
                .then(syncConfigMissions());
    }

    private Mono<Void> syncConfigMissions()
    {
        return this.missionRepository.findAll()
                .map(this.missionConverter::convertToDomainMission)
                .filter(Mission::isEnabled)
                .collectList()
                .map(missions -> {
                    ArmaServerConfig armaServerConfig = this.serverConfigStorage.getServerConfig();
                    armaServerConfig.setMissions(new ArrayList<>(missions.stream()
                            .map(this.missionConverter::convertToArmaMissionObject)
                            .toList()));
                    this.serverConfigStorage.saveServerConfig(armaServerConfig);
                    return true;
                })
                .then();

    }
}
