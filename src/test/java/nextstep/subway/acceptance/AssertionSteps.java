package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class AssertionSteps {

    public static void 구간_생성_요청_실패(ExtractableResponse<Response> response, HttpStatus httpStatus) {
        int statusCode = response.statusCode();
        assertThat(statusCode).isEqualTo(httpStatus.value());
    }

    public static void 구간_생성_요청_성공(ExtractableResponse<Response> response) {
        int statusCode = response.statusCode();
        assertThat(statusCode).isEqualTo(HttpStatus.OK.value());
    }

    public static void 구간_생성_요청_후_역_목록_확인(ExtractableResponse<Response> response, Long... expected) {
        List<Long> idsOfStations = response.jsonPath().getList("stations.id", Long.class);

        assertAll(
                () -> 구간_생성_요청_성공(response),
                () -> 지하철_역_목록_순서_일치_검증(idsOfStations, expected)
        );

    }



    public static void 지하철_역_목록_순서_일치_검증(List<Long> idsOfStations, Long... expected) {
        assertThat(idsOfStations).containsExactly(expected);
    }
}
