package pl.bartlomiejstepien.armaserverwebgui;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.RestTestClient;
import pl.bartlomiejstepien.armaserverwebgui.application.security.jwt.JwtService;
import pl.bartlomiejstepien.armaserverwebgui.config.AswgTestConfiguration;
import pl.bartlomiejstepien.armaserverwebgui.application.security.AswgAuthority;
import pl.bartlomiejstepien.armaserverwebgui.domain.user.UserService;
import pl.bartlomiejstepien.armaserverwebgui.domain.user.dto.AswgUserWithPassword;
import pl.bartlomiejstepien.armaserverwebgui.repository.AuthorityRepository;
import pl.bartlomiejstepien.armaserverwebgui.repository.UserAuthorityRepository;
import pl.bartlomiejstepien.armaserverwebgui.repository.UserRepository;

import java.time.OffsetDateTime;
import java.util.EnumSet;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = AswgTestConfiguration.class)
@AutoConfigureRestTestClient
public abstract class BaseIntegrationTest
{
    protected static final String TEST_USER_NAME = "test_user";

    private static final AswgUserWithPassword TEST_USER = AswgUserWithPassword.builder()
            .username(TEST_USER_NAME)
            .password("TEST_PASS")
            .authorities(EnumSet.allOf(AswgAuthority.class))
            .createdDate(OffsetDateTime.now())
            .build();

    @LocalServerPort
    protected int serverPort;

    @Autowired
    protected JwtService jwtService;
    @Autowired
    protected RestTestClient restTestClient;
    @Autowired
    protected UserService userService;
    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected UserAuthorityRepository userAuthorityRepository;
    @Autowired
    protected AuthorityRepository authorityRepository;

    @BeforeEach
    public void setUpTestUser()
    {
        userAuthorityRepository.deleteAll();
        userRepository.deleteAll();
        userService.addNewUser(TEST_USER);
    }

    protected String createJwtForTestUser()
    {
        return jwtService.createJwt(TEST_USER);
    }

    protected ResponseEntity<String> getAuthenticatedRequest(String url)
    {
        return getAuthenticatedRequest(url, String.class);
    }

    protected <T> ResponseEntity<T> getAuthenticatedRequest(String url, Class<T> responseType)
    {
        return ResponseEntity.ofNullable(restTestClient.get()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + createJwtForTestUser())
                .exchange()
                .returnResult(responseType)
                .getResponseBody());
    }

    protected ResponseEntity<String> postAuthenticatedRequest(String url, Object body, String contentType) {
        return postAuthenticatedRequest(url, body, contentType, String.class);
    }

    protected ResponseEntity<String> postAuthenticatedRequest(String url, Object body)
    {
        return postAuthenticatedRequest(url, body, MediaType.APPLICATION_JSON_VALUE);
    }

    protected <T> ResponseEntity<T> postAuthenticatedRequest(String url, Object body, String contentType, Class<T> responseType)
    {
        var result = restTestClient.post()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + createJwtForTestUser())
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .body(body)
                .exchange()
                .returnResult(responseType);
        return ResponseEntity.status(result.getStatus())
                .body(result.getResponseBody());
    }
}
