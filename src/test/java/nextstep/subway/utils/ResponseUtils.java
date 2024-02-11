package nextstep.subway.utils;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;

public class ResponseUtils {

    public static void 응답의_STATUS_검증(ExtractableResponse<Response> response, HttpStatus status) {
        assertThat(response.statusCode()).isEqualTo(status.value());
    }

    public static long 응답에서_id_조회(ExtractableResponse<Response> response) {
        return response.jsonPath().getLong("id");
    }
}

