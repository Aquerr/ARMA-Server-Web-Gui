package pl.bartlomiejstepien.armaserverwebgui.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.aquerr.steamwebapiclient.SteamPublishedFileWebApiClient;
import io.github.aquerr.steamwebapiclient.SteamWebApiClient;
import io.github.aquerr.steamwebapiclient.request.WorkShopQueryFilesRequest;
import io.github.aquerr.steamwebapiclient.response.WorkShopQueryResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.BodyInserters;
import pl.bartlomiejstepien.armaserverwebgui.BaseIntegrationTest;
import pl.bartlomiejstepien.armaserverwebgui.TestUtils;
import pl.bartlomiejstepien.armaserverwebgui.domain.steam.SteamUtils;

import static org.mockito.BDDMockito.given;

class WorkshopRestControllerTest extends BaseIntegrationTest
{
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SteamPublishedFileWebApiClient publishedFileWebApiClient;
    @MockBean
    private SteamWebApiClient steamWebApiClient;

    @BeforeEach
    void setUp()
    {

    }

    @Test
    void queryShouldReturnWorkshopMods() throws JsonProcessingException
    {
        // given
        String authToken = jwtService.createJwt("test_user");

        WorkShopQueryResponse workShopQueryResponse = objectMapper.readValue(
                TestUtils.loadJsonIntegrationContractFor("workshop/workshop_query_steam_response.json"),
                WorkShopQueryResponse.class);

        given(steamWebApiClient.getSteamPublishedFileWebApiClient()).willReturn(publishedFileWebApiClient);
        given(publishedFileWebApiClient.queryFiles(ArgumentMatchers.refEq(WorkShopQueryFilesRequest.builder()
                    .appId(SteamUtils.ARMA_APP_ID)
                    .cursor("*")
                    .numPerPage(10)
                    .searchText("search_phrase")
                    .returnPreviews(true)
                    .queryType(WorkShopQueryFilesRequest.PublishedFileQueryType.RANKED_BY_TOTAL_UNIQUE_SUBSCRIPTIONS)
                    .fileType(WorkShopQueryFilesRequest.PublishedFileInfoMatchingFileType.ITEMS)
                .build())))
                .willReturn(workShopQueryResponse);

        //TOOD: Enable when steam-web-api-client will allow to accept base-url
//        wireMockServer.stubFor(get("https://api.steampowered.com/IPublishedFileService/QueryFiles/v1")
//                .withQueryParams(Map.of(
//                        "key", equalTo("ABC123MYTOKEN"),
//                        "search_text", equalTo("search_phrase"),
//                        "appId", equalTo(String.valueOf(SteamUtils.ARMA_APP_ID)),
//                        "cursor", equalTo("*"),
//                        "return_previews", equalTo("true"),
//                        "numPerPage", equalTo("10"),
//                        "fileType", equalTo("0"),
//                        "queryType", equalTo("9")
//                )).willReturn(aResponse()
//                        .withBody(TestUtils.loadJsonIntegrationContractFor("workshop/workshop_query_steam_response.json"))
//                        .withStatus(200)));

        // when
        // then
        var request = new WorkshopRestController.WorkshopQueryRequest();
        request.setSearchText("search_phrase");

        webTestClient.post()
                .uri("http://localhost:" + serverPort + "/api/v1/workshop/query")
                .body(BodyInserters.fromValue(request))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + authToken)
                .exchange()
                .expectBody()
                .json(TestUtils.loadJsonIntegrationContractFor("workshop/workshop_query_response.json"));

        wireMockServer.shutdownServer();
    }
}