package pl.bartlomiejstepien.armaserverwebgui.domain.server.mod;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.bartlomiejstepien.armaserverwebgui.application.config.ASWGConfig;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.InstalledMod;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.WorkshopModInstallationRequest;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.mod.model.WorkshopModInstallationStatus;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.mod.ModMetaFile;
import pl.bartlomiejstepien.armaserverwebgui.domain.server.storage.mod.ModStorage;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.SteamService;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.exception.CouldNotInstallWorkshopModException;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.ArmaWorkshopMod;
import pl.bartlomiejstepien.armaserverwebgui.repository.InstalledModRepository;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static java.lang.String.format;

@RequiredArgsConstructor
@Service
@Slf4j
public class WorkShopModInstallService
{
    private final Queue<WorkshopModInstallationRequest> workshopModInstallationRequestQueue = new LinkedBlockingQueue<>();
    private final WorkShopInstallRetryPolicy workShopInstallRetryPolicy;
    private final SteamService steamService;
    private final ModStorage modStorage;
    private final ASWGConfig aswgConfig;
    private final InstalledModRepository installedModRepository;
    private final WorkshopModInstallProgressWebsocketHandler workshopModInstallProgressWebsocketHandler;
    private WorkshopModInstallationRequest currentInstallationRequest;

    public void queueWorkshopModInstallation(WorkshopModInstallationRequest request)
    {
        if (this.workshopModInstallationRequestQueue.stream().noneMatch(installRequest -> installRequest.getFileId() == request.getFileId()))
        {
            log.info("Queue mod installation: {}", request);
            this.workshopModInstallationRequestQueue.add(request);
            publishMessage(new WorkshopModInstallationStatus(request.getFileId(), 0));
        }
        else
        {
            log.info("Mod installation {} already queued. Skipping.", request);
        }
    }

    public List<WorkshopModInstallationRequest> getWorkShopModInstallRequests()
    {
        List<WorkshopModInstallationRequest> requests = new ArrayList<>();
        if (this.currentInstallationRequest != null)
        {
            requests.add(this.currentInstallationRequest);
        }
        requests.addAll(this.workshopModInstallationRequestQueue);
        return requests;
    }

    private void publishMessage(WorkshopModInstallationStatus status)
    {
        this.workshopModInstallProgressWebsocketHandler.publishInstallationStatus(status);
    }

    @Scheduled(fixedDelay = 5, timeUnit = TimeUnit.SECONDS)
    private void handleWorkshopModInstallation()
    {
        if (!workshopModInstallationRequestQueue.isEmpty())
        {
            WorkshopModInstallationRequest request = workshopModInstallationRequestQueue.poll();
            this.currentInstallationRequest = request;
            try
            {
                log.info("Starting mod installation for: {}", request);
                handleInstallRequest(request);
            }
            catch (Exception exception)
            {
                exception.printStackTrace();
                if (workShopInstallRetryPolicy.canRetry(request))
                {
                    log.info("Requeue mod installation request: {}", request);
                    queueWorkshopModInstallation(new WorkshopModInstallationRequest(request.getFileId(), request.getTitle(), request.getInstallAttemptCount() + 1));
                }
            }
        }
    }

    private void handleInstallRequest(WorkshopModInstallationRequest request)
    {
        try
        {
            Path steamCmdModFolderPath = this.steamService.downloadModFromWorkshop(request.getFileId());
            Path modDirectoryPath = this.modStorage.copyModFolderFromSteamCmd(steamCmdModFolderPath, Paths.get(this.aswgConfig.getServerDirectoryPath()), request.getTitle());

            saveModInDatabase(request.getFileId(), request.getTitle(), modDirectoryPath);

            publishMessage(new WorkshopModInstallationStatus(request.getFileId(), 100));
        }
        catch (Exception exception)
        {
            throw new CouldNotInstallWorkshopModException(format("Could not install workshop mod with id = %s, name = %s", request.getFileId(), request.getTitle()), exception);
        }
        finally
        {
            this.currentInstallationRequest = null;
        }
    }

    private void saveModInDatabase(long workshopFileId, String modName, Path modDirectory)
    {
        InstalledMod installedMod = this.installedModRepository.findByWorkshopFileId(workshopFileId).block();
        ModMetaFile modMetaFile = modStorage.readModMetaFile(modDirectory);

        InstalledMod.InstalledModBuilder installedModBuilder;
        if (installedMod != null) // Update
        {
            log.info("Mod: {} already exists. Performing update only.", modName);
            installedModBuilder = installedMod.toBuilder();
        }
        else // New mod
        {
            installedModBuilder = InstalledMod.builder();
            installedModBuilder.createdDate(OffsetDateTime.now());
            installedModBuilder.workshopFileId(modMetaFile.getPublishedFileId());
        }

        installedModBuilder.name(modMetaFile.getName());
        installedModBuilder.directoryPath(modDirectory.toAbsolutePath().toString());

        ArmaWorkshopMod armaWorkshopMod = steamService.getWorkshopMod(modMetaFile.getPublishedFileId());
        if (armaWorkshopMod != null)
        {
            installedModBuilder.previewUrl(armaWorkshopMod.getPreviewUrl());
        }

        installedModRepository.save(installedModBuilder.build()).subscribe();
    }
}
