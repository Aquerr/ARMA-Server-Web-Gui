package pl.bartlomiejstepien.armaserverwebgui;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
//import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.wiremock.spring.EnableWireMock;
import org.wiremock.spring.InjectWireMock;
import pl.bartlomiejstepien.armaserverwebgui.application.config.security.JwtService;
import pl.bartlomiejstepien.armaserverwebgui.config.AswgTestConfiguration;
import pl.bartlomiejstepien.armaserverwebgui.application.security.AswgAuthority;
import pl.bartlomiejstepien.armaserverwebgui.domain.user.UserService;
import pl.bartlomiejstepien.armaserverwebgui.domain.user.dto.AswgUserWithPassword;
import pl.bartlomiejstepien.armaserverwebgui.repository.UserAuthorityRepository;
import pl.bartlomiejstepien.armaserverwebgui.repository.UserRepository;

import java.time.OffsetDateTime;
import java.util.EnumSet;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = AswgTestConfiguration.class)
@ContextConfiguration(loader = TestSpringContextLoader.class)
@EnableWireMock
//@AutoConfigureWireMock(port = 0)
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

    @InjectWireMock
//    @Autowired
    protected WireMockServer wireMockServer;
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
}
