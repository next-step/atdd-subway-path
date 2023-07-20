package nextstep.subway.utils;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import org.springframework.http.HttpStatus;

public class CustomAssertions {

    public static void 상태코드_확인(ExtractableResponse<Response> response, HttpStatus httpStatus) {
        Assertions.assertThat(response.statusCode()).isEqualTo(httpStatus.value());
    }

}
