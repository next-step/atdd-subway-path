package nextstep.subway.config;

import nextstep.subway.utils.DatabaseCleanup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ManualCleanAcceptanceTest {

  @Autowired
  protected DatabaseCleanup databaseCleanup;
}
