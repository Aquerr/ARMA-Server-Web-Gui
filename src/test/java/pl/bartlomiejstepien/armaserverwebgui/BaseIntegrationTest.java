package pl.bartlomiejstepien.armaserverwebgui;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import pl.bartlomiejstepien.armaserverwebgui.application.config.security.JwtService;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(loader = TestSpringContextLoader.class)
@AutoConfigureWireMock(port = 0)
public abstract class BaseIntegrationTest
{
    @LocalServerPort
    protected int serverPort;

    @Autowired
    protected WireMockServer wireMockServer;
    @Autowired
    protected JwtService jwtService;
    @Autowired
    protected WebTestClient webTestClient;
}
