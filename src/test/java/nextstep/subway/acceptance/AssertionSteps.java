package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

public class AssertionSteps {

    public static void 응답_코드_검증(ExtractableResponse<Response> response, HttpStatus httpStatus) {
        int statusCode = response.statusCode();
        assertThat(statusCode).isEqualTo(httpStatus.value());
    }
}
