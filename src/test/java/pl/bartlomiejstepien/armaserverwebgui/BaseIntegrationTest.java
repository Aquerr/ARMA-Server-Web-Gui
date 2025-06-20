package pl.bartlomiejstepien.armaserverwebgui;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.MultiValueMap;
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
import java.util.Map;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = AswgTestConfiguration.class)
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
    protected TestRestTemplate testRestTemplate;
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
        return testRestTemplate.exchange(url,
                HttpMethod.GET,
                new HttpEntity<>(null, MultiValueMap.fromSingleValue(Map.of(
                        HttpHeaders.AUTHORIZATION, "Bearer " + createJwtForTestUser()
                ))),
                String.class);
    }
}
