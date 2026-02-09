package pl.bartlomiejstepien.armaserverwebgui.web;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.wiremock.spring.EnableWireMock;
import org.wiremock.spring.InjectWireMock;
import pl.bartlomiejstepien.armaserverwebgui.BaseIntegrationTest;
import pl.bartlomiejstepien.armaserverwebgui.TestUtils;
import pl.bartlomiejstepien.armaserverwebgui.web.request.WorkshopQueryRequest;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;

@EnableWireMock
class WorkshopRestControllerTest extends BaseIntegrationTest
{
    @Autowired
    private CacheManager cacheManager;

    @InjectWireMock
    private WireMockServer wireMockServer;

    @Test
    void queryShouldReturnWorkshopModsFromCache() throws JSONException
    {
        wireMockServer.resetAll();
        cacheManager.getCache("workshop-query").clear();

        // given
        wireMockServer.stubFor(get("/IPublishedFileService/QueryFiles/v1?cursor=*&creator_appid=0&filetype=0&requiredtags=&return_for_sale_data=false&language=0&return_short_description=false&omitted_flags=&child_publishedfileid=0&return_playtime_stats=false&totalonly=false&return_children=true&required_flags=&excludedtags=&return_vote_data=false&key=test-api-key&match_all_tags=false&return_metadata=false&return_tags=false&cache_max_age_seconds=0&numperpage=10&ids_only=false&query_type=3&return_kv_tags=false&appid=107410&days=-1&include_recent_votes_only=false&page=&return_previews=true&search_text=search_phrase")
                .willReturn(aResponse()
                        .withBody(TestUtils.loadJsonIntegrationContractFor("workshop/workshop_query_steam_response.json"))
                        .withStatus(200)));

        // when
        // then
        executeQueryWorkshop(prepareQueryRequest("search_phrase"), "workshop/workshop_query_response.json");
        executeQueryWorkshop(prepareQueryRequest("search_phrase"), "workshop/workshop_query_response.json");

        wireMockServer.verify(1, RequestPatternBuilder.newRequestPattern()
                .withUrl("/IPublishedFileService/QueryFiles/v1?cursor=*&creator_appid=0&filetype=0&requiredtags=&return_for_sale_data=false&language=0&return_short_description=false&omitted_flags=&child_publishedfileid=0&return_playtime_stats=false&totalonly=false&return_children=true&required_flags=&excludedtags=&return_vote_data=false&key=test-api-key&match_all_tags=false&return_metadata=false&return_tags=false&cache_max_age_seconds=0&numperpage=10&ids_only=false&query_type=3&return_kv_tags=false&appid=107410&days=-1&include_recent_votes_only=false&page=&return_previews=true&search_text=search_phrase"));
    }

    @Test
    void queryShouldReturnWorkshopModsNoCache() throws JSONException
    {
        wireMockServer.resetAll();
        cacheManager.getCache("workshop-query").clear();

        // given
        wireMockServer.stubFor(get("/IPublishedFileService/QueryFiles/v1?cursor=*&creator_appid=0&filetype=0&requiredtags=&return_for_sale_data=false&language=0&return_short_description=false&omitted_flags=&child_publishedfileid=0&return_playtime_stats=false&totalonly=false&return_children=true&required_flags=&excludedtags=&return_vote_data=false&key=test-api-key&match_all_tags=false&return_metadata=false&return_tags=false&cache_max_age_seconds=0&numperpage=10&ids_only=false&query_type=3&return_kv_tags=false&appid=107410&days=-1&include_recent_votes_only=false&page=&return_previews=true&search_text=search_phrase")
                .willReturn(aResponse()
                        .withBody(TestUtils.loadJsonIntegrationContractFor("workshop/workshop_query_steam_response.json"))
                        .withStatus(200)));

        wireMockServer.stubFor(get("/IPublishedFileService/QueryFiles/v1?cursor=*&creator_appid=0&filetype=0&requiredtags=&return_for_sale_data=false&language=0&return_short_description=false&omitted_flags=&child_publishedfileid=0&return_playtime_stats=false&totalonly=false&return_children=true&required_flags=&excludedtags=&return_vote_data=false&key=test-api-key&match_all_tags=false&return_metadata=false&return_tags=false&cache_max_age_seconds=0&numperpage=10&ids_only=false&query_type=3&return_kv_tags=false&appid=107410&days=-1&include_recent_votes_only=false&page=&return_previews=true&search_text=another_phrase")
                .willReturn(aResponse()
                        .withBody(TestUtils.loadJsonIntegrationContractFor("workshop/workshop_query_steam_response.json"))
                        .withStatus(200)));

        // when
        // then
        executeQueryWorkshop(prepareQueryRequest("search_phrase"), "workshop/workshop_query_response.json");
        executeQueryWorkshop(prepareQueryRequest("another_phrase"), "workshop/workshop_query_response.json");

        wireMockServer.verify(2, RequestPatternBuilder.allRequests());
    }

    private WorkshopQueryRequest prepareQueryRequest(String searchPhrase)
    {
        return new WorkshopQueryRequest("*", searchPhrase);
    }

    private void executeQueryWorkshop(WorkshopQueryRequest request, String exptectedResponseJsonFile) throws JSONException
    {
        var response = postAuthenticatedRequest(
                "http://localhost:" + serverPort + "/api/v1/workshop/query",
                request,
                MediaType.APPLICATION_JSON_VALUE,
                String.class);
        JSONAssert.assertEquals(
                TestUtils.loadJsonIntegrationContractFor(exptectedResponseJsonFile),
                response.getBody(),
                JSONCompareMode.LENIENT
        );
    }
}