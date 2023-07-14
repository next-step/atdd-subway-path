package nextstep.subway.utils;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;
import nextstep.subway.common.exception.ErrorCode;
import nextstep.subway.common.exception.ErrorResponseDto;

public class StatusCodeAssertions {

    private StatusCodeAssertions() {}

    public static void 응답코드_검증(ExtractableResponse<Response> response, HttpStatus status) {
        assertThat(response.statusCode()).isEqualTo(status.value());
    }

    public static void 에러코드_검증(ExtractableResponse<Response> response, ErrorCode errorCode) {
        ErrorResponseDto errorResponseDto = response.jsonPath().getObject("", ErrorResponseDto.class);
        assertThat(errorResponseDto.getCode()).isEqualTo(errorCode.getCode());
    }
}
