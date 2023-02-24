package nextstep.subway.acceptance;

import nextstep.subway.utils.DatabaseCleanup;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.fixture.ParamFixtures.*;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class AcceptanceTest {
    @Autowired
    private DatabaseCleanup databaseCleanup;

    @BeforeEach
    public void setUp() {
        databaseCleanup.execute();
    }

    protected static Map<String, String> createLineCreateParams(String lineName, String lineColor, Long upStationId, Long downStationId, int distance) {
        Map<String, String> lineCreateParams = new HashMap<>();
        lineCreateParams.put(PARAM_NAME, lineName);
        lineCreateParams.put(PARAM_COLOR, lineColor);
        lineCreateParams.put(PARAM_UP_STATION_ID, String.valueOf(upStationId));
        lineCreateParams.put(PARAM_DOWN_STATION_ID, String.valueOf(downStationId));
        lineCreateParams.put(PARAM_DISTANCE, String.valueOf(distance));
        return lineCreateParams;
    }

    protected Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put(PARAM_UP_STATION_ID, String.valueOf(upStationId));
        params.put(PARAM_DOWN_STATION_ID, String.valueOf(downStationId));
        params.put(PARAM_DISTANCE, String.valueOf(distance));
        return params;
    }
}
