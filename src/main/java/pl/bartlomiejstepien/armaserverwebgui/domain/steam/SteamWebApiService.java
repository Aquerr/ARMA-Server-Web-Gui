package pl.bartlomiejstepien.armaserverwebgui.domain.steam;

import io.github.aquerr.steamwebapiclient.SteamWebApiClient;
import io.github.aquerr.steamwebapiclient.request.PublishedFileDetailsRequest;
import io.github.aquerr.steamwebapiclient.request.WorkShopQueryFilesRequest;
import io.github.aquerr.steamwebapiclient.response.PublishedFileDetailsResponse;
import io.github.aquerr.steamwebapiclient.response.WorkShopQueryResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.WorkshopMod;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.ArmaWorkshopQueryResponse;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.WorkshopQueryParams;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class SteamWebApiService
{
    private final SteamWebApiClient steamWebApiClient;
    private final ArmaWorkshopModConverter armaWorkshopModConverter;

    @Cacheable(cacheNames = "workshop-query")
    public ArmaWorkshopQueryResponse queryWorkshopMods(WorkshopQueryParams params)
    {
        WorkShopQueryResponse workShopQueryResponse = steamWebApiClient.getSteamPublishedFileWebApiClient().queryFiles(WorkShopQueryFilesRequest.builder()
                .appId(SteamUtils.ARMA_APP_ID)
                .cursor(StringUtils.hasText(params.getCursor()) ? params.getCursor() : "*")
                .numPerPage(10)
                .searchText(StringUtils.hasText(params.getSearchText()) ? params.getSearchText() : null)
                .returnPreviews(true)
                .queryType(WorkShopQueryFilesRequest.PublishedFileQueryType.RANKED_BY_TOTAL_UNIQUE_SUBSCRIPTIONS)
                .fileType(WorkShopQueryFilesRequest.PublishedFileInfoMatchingFileType.ITEMS)
                .build());

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
            return Optional.ofNullable(this.steamWebApiClient.getSteamRemoteStorageClient().getPublishedFileDetails(new PublishedFileDetailsRequest(List.of(modId))))
                    .map(PublishedFileDetailsResponse::getResponse)
                    .map(PublishedFileDetailsResponse.QueryFilesResponse::getPublishedFileDetails)
                    .filter(list -> !list.isEmpty())
                    .map(List::getFirst)
                    .map(this.armaWorkshopModConverter::convert)
                    .orElse(null);
        }
        catch (Exception exception)
        {
            log.warn("Could not fetch mod info from workshop. Reason: {}", exception.getMessage());
            return null;
        }
    }
}
