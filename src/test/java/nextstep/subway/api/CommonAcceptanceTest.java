package nextstep.subway.api;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;

import nextstep.subway.utils.data.DatabaseCleanupExecutor;

/**
 * @author : Rene Choi
 * @since : 2024/02/02
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Component
public class CommonAcceptanceTest {

	@Autowired
	private DatabaseCleanupExecutor databaseCleanupExecutor;

	@BeforeEach
	void databaseCleanup() {
		databaseCleanupExecutor.execute();
	}
}
