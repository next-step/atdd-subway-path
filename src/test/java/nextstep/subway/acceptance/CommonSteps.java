package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public class CommonSteps {

    public static Long getIdFromResponse(ExtractableResponse<Response> response) {
        return response.jsonPath().getLong("id");
    }
}
