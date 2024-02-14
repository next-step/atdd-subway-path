package nextstep.subway.common;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CommonAcceptanceTest {

    @LocalServerPort
    private int port;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @BeforeEach
    void beforeEach() {
        RestAssured.port = port;
        databaseCleanUp.afterPropertiesSet();
        databaseCleanUp.execute();
    }
}
