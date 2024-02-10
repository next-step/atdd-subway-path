package subway.acceptance;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import io.restassured.RestAssured;
import subway.utils.database.DatabaseCleanup;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AcceptanceTest {
	@LocalServerPort
	private int port;

	@Autowired
	private DatabaseCleanup databaseCleanup;

	@BeforeEach
	public void beforeEach() {
		if (RestAssured.UNDEFINED_PORT == RestAssured.port) {
			RestAssured.port = port;
			databaseCleanup.afterPropertiesSet();
		}

		databaseCleanup.execute();
	}
}
