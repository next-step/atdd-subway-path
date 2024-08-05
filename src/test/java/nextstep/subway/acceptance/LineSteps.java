package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.presentation.LineRequest;
import nextstep.subway.presentation.LineResponse;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LineSteps {
    static ExtractableResponse<Response> 지하철_노선_생성(LineRequest lineRequest) {
        return RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract();
    }

    static List<String> 지하철_노선_내_전체_지하철_역_이름_찾기() {
        return RestAssured.given().log().all()
                .when().get("/lines")
                .then().log().all()
                .extract().jsonPath().getList("name", String.class);
    }

    static LineResponse 노선_아이디로_지하철_노선_찾기(Long lineId) {
        return RestAssured.given().log().all()
                .when().get("/lines/" + lineId)
                .then().log().all()
                .extract().as(LineResponse.class);
    }

    static void 지하철_노선_정보_수정(Long lineId, String name, String color) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);

        RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/lines/" + lineId)
                .then().log().all();
    }

    static void 지하철_노선_삭제(String lineId) {
        RestAssured.given().log().all()
                .when().delete("/lines/" + lineId)
                .then().log().all();
    }

    static void 지하철_노선_내_지하철_역_삭제(Long lineId, Long stationId) {
        RestAssured.given().log().all()
                .when().delete("/lines/" + lineId + "/stations/" + stationId)
                .then().log().all();
    }
}
