package pl.bartlomiejstepien.armaserverwebgui.web;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.BodyInserters;
import pl.bartlomiejstepien.armaserverwebgui.BaseIntegrationTest;
import pl.bartlomiejstepien.armaserverwebgui.TestUtils;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;

class WorkshopRestControllerTest extends BaseIntegrationTest
{
    @Test
    void queryShouldReturnWorkshopMods()
    {
        // given
        String authToken = createUserAndJwt();

        wireMockServer.stubFor(get("/IPublishedFileService/QueryFiles/v1?cursor=*&creator_appid=0&filetype=0&requiredtags=&return_for_sale_data=false&language=0&return_short_description=false&omitted_flags=&child_publishedfileid=0&return_playtime_stats=false&totalonly=false&return_children=false&required_flags=&excludedtags=&return_vote_data=false&match_all_tags=false&return_metadata=false&return_tags=false&cache_max_age_seconds=0&numperpage=10&ids_only=false&query_type=9&return_kv_tags=false&appid=107410&days=0&include_recent_votes_only=false&page=&return_previews=true&search_text=search_phrase")
                .willReturn(aResponse()
                        .withBody(TestUtils.loadJsonIntegrationContractFor("workshop/workshop_query_steam_response.json"))
                        .withStatus(200)));

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