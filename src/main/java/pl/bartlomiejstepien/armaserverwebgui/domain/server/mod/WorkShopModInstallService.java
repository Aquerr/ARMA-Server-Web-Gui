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
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.exception.CouldNotDownloadWorkshopModException;
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
            }
        }
    }

    private void handleInstallRequest(WorkshopModInstallationRequest request)
    {
        try
        {
            boolean updateOnly = this.installedModRepository.findById(request.getFileId()).block() != null;

            Path steamCmdModFolderPath = this.steamService.downloadModFromWorkshop(request.getFileId());
            Path modDirectoryPath = this.modStorage.copyModFolderFromSteamCmd(steamCmdModFolderPath, Paths.get(this.aswgConfig.getServerDirectoryPath()), request.getTitle());

            if (!updateOnly) {
                saveModInDatabase(modDirectoryPath);
            }

            publishMessage(new WorkshopModInstallationStatus(request.getFileId(), 100));
            this.currentInstallationRequest = null;
        }
        catch (CouldNotDownloadWorkshopModException e)
        {
            throw new CouldNotInstallWorkshopModException(format("Could not install workshop mod with id = %s, name = %s", request.getFileId(), request.getTitle()), e);
        }
    }

    private void saveModInDatabase(Path modDirectory)
    {
        ModMetaFile modMetaFile = modStorage.readModMetaFile(modDirectory);

        InstalledMod.InstalledModBuilder installedModBuilder = InstalledMod.builder();
        installedModBuilder.publishedFileId(modMetaFile.getPublishedFileId());
        installedModBuilder.name(modMetaFile.getName());
        installedModBuilder.directoryPath(modDirectory.toAbsolutePath().toString());
        installedModBuilder.createdDate(OffsetDateTime.now());

        ArmaWorkshopMod armaWorkshopMod = steamService.getWorkshopMod(modMetaFile.getPublishedFileId());
        if (armaWorkshopMod != null)
        {
            installedModBuilder.previewUrl(armaWorkshopMod.getPreviewUrl());
        }
        InstalledMod installedMod = installedModBuilder.build();

        installedModRepository.save(installedMod).subscribe();
    }
}
