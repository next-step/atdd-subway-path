package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.assertj.core.api.AbstractIntegerAssert;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

public class AssertResponse {

    public static void 응답_실패_검증(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    public static AbstractIntegerAssert<?> 응답_성공_검증(ExtractableResponse<Response> response) {
        return assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }
}
