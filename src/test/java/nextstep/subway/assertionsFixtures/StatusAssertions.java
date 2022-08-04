package nextstep.subway.assertionsFixtures;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.enums.exceptions.ErrorCode;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

public class StatusAssertions {
    public static void 상태_검증(ErrorCode errorCode, HttpStatus httpStatus, ExtractableResponse<Response> response) {
        Assertions.assertAll(() -> {
            assertThat(response.jsonPath().getString("message")).isEqualTo(errorCode.getMessage());
            assertThat(response.statusCode()).isEqualTo(httpStatus.value());
        });
    }

    public static void 상태_검증(HttpStatus httpStatus, ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(httpStatus.value());
    }
}
