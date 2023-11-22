package pl.bartlomiejstepien.armaserverwebgui.domain.steam;

import io.github.aquerr.steamwebapiclient.SteamWebApiClient;
import io.github.aquerr.steamwebapiclient.request.PublishedFileDetailsRequest;
import io.github.aquerr.steamwebapiclient.request.WorkShopQueryFilesRequest;
import io.github.aquerr.steamwebapiclient.response.PublishedFileDetailsResponse;
import io.github.aquerr.steamwebapiclient.response.WorkShopQueryResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.ArmaWorkshopMod;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.ArmaWorkshopQueryResponse;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.model.WorkshopQueryParams;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SteamWebApiService
{
    private final SteamWebApiClient steamWebApiClient;
    private final ArmaWorkshopModConverter armaWorkshopModConverter;

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
        List<ArmaWorkshopMod> armaWorkshopMods = Collections.emptyList();
        if (workShopQueryResponse != null)
        {
            nextPageCursor = workShopQueryResponse.getResponse().getNextCursor();
            armaWorkshopMods = workShopQueryResponse.getResponse().getPublishedFileDetails().stream()
                    .map(armaWorkshopModConverter::convert)
                    .toList();
        }

        return ArmaWorkshopQueryResponse.builder()
                .nextCursor(nextPageCursor)
                .mods(armaWorkshopMods)
                .build();
    }

    public ArmaWorkshopMod getWorkshopMod(long modId)
    {
        return Optional.ofNullable(this.steamWebApiClient.getSteamRemoteStorageClient().getPublishedFileDetails(new PublishedFileDetailsRequest(List.of(modId))))
                .map(PublishedFileDetailsResponse::getResponse)
                .map(PublishedFileDetailsResponse.QueryFilesResponse::getPublishedFileDetails)
                .filter(list -> !list.isEmpty())
                .map(list -> list.get(0))
                .map(this.armaWorkshopModConverter::convert)
                .orElse(null);
    }
}
