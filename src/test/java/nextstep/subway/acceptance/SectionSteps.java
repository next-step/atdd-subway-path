package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public class SectionSteps {
    public static ExtractableResponse<Response> 특정_구간_조회(Long lineId) {

        return RestAssured
                .given().log().all()
                .when().pathParam("lineId", lineId)
                .get("lines/{lineId}/sections")
                .then().log().all()
                .extract();
    }

    public static String 라인_아이디(ExtractableResponse<Response> 특정_라인) {
        return 특정_라인.jsonPath().getString("id");
    }
}
