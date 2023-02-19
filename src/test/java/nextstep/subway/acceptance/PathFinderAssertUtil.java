package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class PathFinderAssertUtil {

    public static void 지하철_경로_조회_검증(ExtractableResponse<Response> response, int expectedDistance, Long... expectedStationIds) {
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getDouble("distance")).isEqualTo(expectedDistance),
                () -> assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(expectedStationIds)
        );
    }
}
