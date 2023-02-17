package nextstep.subway.acceptance;

import static nextstep.subway.acceptance.LineSteps.지하철_노선_목록_조회_요청;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpStatus;

public class LineAcceptanceAssert {

    protected static void 지하철_노선_생성_요청_검증(final ExtractableResponse<Response> response, final String name) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        ExtractableResponse<Response> listResponse = 지하철_노선_목록_조회_요청();

        assertThat(listResponse.jsonPath().getList("name")).contains(name);
    }

    protected static void 지하철_노선_목록_조회_검증(final ExtractableResponse<Response> response, final String... names) {
        Assertions.assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getList("name")).contains(names)
        );
    }

    protected static void 지하철_노선_조회_검증(final ExtractableResponse<Response> response, final String name) {
        Assertions.assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getString("name")).isEqualTo(name)
        );
    }

    protected static void 지하철_노선_삭제_요청_검증(final ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
