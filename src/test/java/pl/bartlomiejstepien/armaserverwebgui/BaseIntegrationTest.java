package pl.bartlomiejstepien.armaserverwebgui;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import pl.bartlomiejstepien.armaserverwebgui.application.config.security.JwtService;
import pl.bartlomiejstepien.armaserverwebgui.config.AswgTestConfiguration;
import pl.bartlomiejstepien.armaserverwebgui.application.security.AswgAuthority;
import pl.bartlomiejstepien.armaserverwebgui.domain.user.UserService;
import pl.bartlomiejstepien.armaserverwebgui.domain.user.dto.AswgUserWithPassword;
import pl.bartlomiejstepien.armaserverwebgui.interfaces.user.repository.UserAuthorityRepository;
import pl.bartlomiejstepien.armaserverwebgui.interfaces.user.repository.UserRepository;

import java.time.OffsetDateTime;
import java.util.EnumSet;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = AswgTestConfiguration.class)
@ContextConfiguration(loader = TestSpringContextLoader.class)
@AutoConfigureWireMock(port = 0)
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
    protected WireMockServer wireMockServer;
    @Autowired
    protected JwtService jwtService;
    @Autowired
    protected WebTestClient webTestClient;
    @Autowired
    protected UserService userService;
    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected UserAuthorityRepository userAuthorityRepository;

    @BeforeEach
    public void setUpTestUser()
    {
        userAuthorityRepository.deleteAll().block();
        userRepository.deleteAll().block();
        userService.addNewUser(TEST_USER).block();
    }

    protected String createUserAndJwt()
    {
        return jwtService.createJwt(TEST_USER);
    }
}
