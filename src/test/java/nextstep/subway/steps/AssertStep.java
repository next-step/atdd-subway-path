package nextstep.subway.steps;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import static org.assertj.core.api.Assertions.assertThat;

public class AssertStep {

    public static void 에러코드400을_검증한다(ExtractableResponse<Response> response, RuntimeException e) {
        assertThat(response.statusCode()).isEqualTo(400);
        assertThat(response.jsonPath().getString("message")).isEqualTo(e.getMessage());
    }
}
