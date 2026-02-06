package pl.bartlomiejstepien.armaserverwebgui.domain.steam;

import io.github.aquerr.steamwebapiclient.exception.HttpClientException;
import io.github.aquerr.steamwebapiclient.request.GetDetailsRequest;
import io.github.aquerr.steamwebapiclient.request.PublishedFileDetailsRequest;
import io.github.aquerr.steamwebapiclient.request.WorkShopQueryFilesRequest;
import io.github.aquerr.steamwebapiclient.response.FileDetailsResponse;
import io.github.aquerr.steamwebapiclient.response.PublishedFileDetailsResponse;
import io.github.aquerr.steamwebapiclient.response.WorkShopQueryResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.exception.MissingSteamApiKeyException;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.ArmaWorkshopQueryResponse;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.SteamWebApiClientWrapper;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.WorkshopMod;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.WorkshopQueryParams;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;

@Slf4j
@Service
@AllArgsConstructor
public class SteamWebApiService
{
    private final SteamWebApiClientWrapper steamWebApiClientWrapper;
    private final ArmaWorkshopModConverter armaWorkshopModConverter;

    @Cacheable(cacheNames = "workshop-query")
    public ArmaWorkshopQueryResponse queryWorkshopMods(WorkshopQueryParams params)
    {
        WorkShopQueryResponse workShopQueryResponse;
        try
        {
            workShopQueryResponse = steamWebApiClientWrapper.getSteamWebApiClient().getSteamPublishedFileWebApiClient()
                    .queryFiles(WorkShopQueryFilesRequest.builder()
                    .appId(SteamUtils.ARMA_APP_ID)
                    .cursor(StringUtils.hasText(params.getCursor()) ? params.getCursor() : "*")
                    .numPerPage(10)
                    .searchText(StringUtils.hasText(params.getSearchText()) ? params.getSearchText() : null)
                    .returnPreviews(true)
                    .queryType(WorkShopQueryFilesRequest.PublishedFileQueryType.RANKED_BY_TOTAL_UNIQUE_SUBSCRIPTIONS)
                    .fileType(WorkShopQueryFilesRequest.PublishedFileInfoMatchingFileType.ITEMS)
                    .returnChildren(true)
                    .build());
        }
        catch (HttpClientException exception)
        {
            if (exception.getStatusCode() == HttpStatus.FORBIDDEN.value())
            {
                throw new MissingSteamApiKeyException();
            }
            throw exception;
        }

        String nextPageCursor = null;
        List<WorkshopMod> workshopMods = Collections.emptyList();
        if (workShopQueryResponse != null)
        {
            nextPageCursor = workShopQueryResponse.getResponse().getNextCursor();
            workshopMods = workShopQueryResponse.getResponse().getPublishedFileDetails().stream()
                    .map(armaWorkshopModConverter::convert)
                    .toList();
        }

        return ArmaWorkshopQueryResponse.builder()
                .nextCursor(nextPageCursor)
                .mods(workshopMods)
                .build();
    }

    @Cacheable("workshop-get-mod")
    @Nullable
    public WorkshopMod getWorkshopMod(long modId)
    {
        try
        {
            log.info("Fetching workshop mod info for mod id: {}", modId);
            WorkshopMod workshopMod = Optional.ofNullable(this.steamWebApiClientWrapper.getSteamWebApiClient().getSteamPublishedFileWebApiClient()
                            .getDetails(GetDetailsRequest.builder()
                                    .appId(SteamUtils.ARMA_APP_ID)
                                    .publishedFileIds(List.of(modId))
                                    .includeChildren(true)
                                    .build()))
                    .map(FileDetailsResponse::getResponse)
                    .map(FileDetailsResponse.QueryFilesResponse::getPublishedFileDetails)
                    .filter(list -> !list.isEmpty())
                    .map(List::getFirst)
                    .map(this.armaWorkshopModConverter::convert)
                    .orElse(null);
            log.info("Got workshop info for mod: {}", Optional.ofNullable(workshopMod).map(WorkshopMod::getTitle).orElse(null));
            return workshopMod;
        }
        catch (Exception exception)
        {
            log.warn("Could not fetch mod info from workshop. Reason: {}", exception.getMessage());
            return null;
        }
    }

    @Cacheable("workshop-get-mods")
    public List<WorkshopMod> getWorkshopMods(List<Long> modIds)
    {
        try
        {
            log.info("Fetching workshop mod info for mod ids: {}", modIds);
            List<WorkshopMod> workshopMods = Optional.ofNullable(this.steamWebApiClientWrapper.getSteamWebApiClient().getSteamRemoteStorageClient()
                            .getPublishedFileDetails(new PublishedFileDetailsRequest(List.copyOf(modIds))))
                    .map(PublishedFileDetailsResponse::getResponse)
                    .map(PublishedFileDetailsResponse.QueryFilesResponse::getPublishedFileDetails)
                    .orElse(List.of())
                    .stream()
                    .map(this.armaWorkshopModConverter::convert)
                    .toList();
            log.info("Got workshop info for mods: {}", workshopMods);
            return workshopMods;
        }
        catch (Exception exception)
        {
            log.warn("Could not fetch mod info from workshop. Reason: {}", exception.getMessage());
            return List.of();
        }
    }
}
