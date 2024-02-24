package nextstep.subway.steps;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import static org.assertj.core.api.Assertions.assertThat;

public class AssertStep {

    public static void assertAlreadyRegistered(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(400);
        assertThat(response.jsonPath().getString("message")).isEqualTo("이미 등록된 상태입니다.");
    }
}
