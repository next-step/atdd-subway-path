package nextstep.subway.common;

import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.utils.DatabaseCleanup;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
public class IntegrationUnitTest {

	@Autowired
	private DatabaseCleanup databaseCleanup;

	@AfterEach
	void dataBaseClean() {
		databaseCleanup.execute();
	}
}
