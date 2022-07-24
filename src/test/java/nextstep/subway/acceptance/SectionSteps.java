package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public class SectionSteps {

    public static ExtractableResponse<Response> 지하철_노선의_구간목록_조회_요청(Long lineId) {
        return RestAssured
                .given().log().all()
                .when().get("/lines/sections/{lineId}", lineId)
                .then().log().all().extract();
    }
}
