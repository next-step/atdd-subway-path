package nextstep.subway.acceptance;

import nextstep.subway.domain.dto.PathResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class PathFinderAssertUtil {

    public static void 지하철_경로_조회_검증(PathResponse response, int expectedDistance, Long... expectedStationIds) {
        assertAll(
                () -> assertThat(response.getDistance()).isEqualTo(expectedDistance),
                () -> assertThat(response.getStations()).extracting("id").containsExactly(expectedStationIds)
        );
    }
}
