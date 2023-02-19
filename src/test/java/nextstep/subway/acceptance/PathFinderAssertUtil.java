package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;

import static nextstep.subway.common.constants.ErrorConstant.NOT_LINKED_STATION;
import static nextstep.subway.common.constants.ErrorConstant.SAME_STATION;
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

    public static void 지하철_경로_조회_같은역으로_조회_예외(ExtractableResponse<Response> response) {
        지하철_경로_조회_예외(response, SAME_STATION);
    }

    public static void 지하철_경로_조회_연결되지역을_조회_예외(ExtractableResponse<Response> response) {
        지하철_경로_조회_예외(response, NOT_LINKED_STATION);
    }

    private static void 지하철_경로_조회_예외(ExtractableResponse<Response> response, String message) {
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(response.body().asString()).isEqualTo(message)
        );
    }
}
