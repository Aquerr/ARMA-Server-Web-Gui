package pl.bartlomiejstepien.armaserverwebgui;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import pl.bartlomiejstepien.armaserverwebgui.application.config.security.JwtService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties =
        {
                "wiremock.server.port=${server.port}"
        })
@AutoConfigureWireMock(port = 0)
public abstract class BaseIntegrationTest
{
    @LocalServerPort
    protected int serverPort;

    @Autowired
    protected WireMockServer wireMockServer;
    @Autowired
    protected JwtService jwtService;
}
