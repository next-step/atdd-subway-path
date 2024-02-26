package nextstep.subway.acceptance.line;


import static nextstep.subway.acceptance.ResponseParser.getIdFromResponse;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import org.springframework.http.MediaType;

public class LineAcceptanceTestHelper {

    public static ExtractableResponse<Response> 노선_생성_요청(HashMap<String, String> params) {
        return RestAssured.given().log().all()
                          .body(params)
                          .contentType(MediaType.APPLICATION_JSON_VALUE)
                          .when().post("/lines")
                          .then().log().all()
                          .extract();
    }

    public static HashMap<String, String> 노선_파라미터_생성(String name, String upStationId, String downStationId) {
        HashMap<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", "bg-red-600");
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", "10");
        return params;
    }

    public static HashMap<String, String> 노선_파라미터_생성(String name, String upStationId, String downStationId, String distance) {
        HashMap<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", "bg-red-600");
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);
        return params;
    }

    public static ExtractableResponse<Response> 노선목록_조회_요청() {
        return RestAssured.given().log().all()
                          .contentType(MediaType.APPLICATION_JSON_VALUE)
                          .when().get("/lines")
                          .then().log().all()
                          .extract();
    }

    public static ExtractableResponse<Response> 노선_단건조회_요청(ExtractableResponse<Response> response) {
        return RestAssured.given().log().all()
                          .contentType(MediaType.APPLICATION_JSON_VALUE)
                          .when().get("/lines/" + getIdFromResponse(response))
                          .then().log().all()
                          .extract();
    }
    public static ExtractableResponse<Response> 노선_단건조회_요청(String lineId) {
        return RestAssured.given().log().all()
                          .contentType(MediaType.APPLICATION_JSON_VALUE)
                          .when().get("/lines/" + lineId)
                          .then().log().all()
                          .extract();
    }

    public static ExtractableResponse<Response> 노선_수정_요청(HashMap<String, String> updateParam,
        Long id) {
        return RestAssured.given().log().all()
                          .body(updateParam)
                          .contentType(MediaType.APPLICATION_JSON_VALUE)
                          .when().put("/lines/" + id)
                          .then().log().all()
                          .extract();
    }

    public static HashMap<String, String> 노선수정_파라미터_생성() {
        HashMap<String, String> updateParam = new HashMap<>();
        updateParam.put("name", "다른분당선");
        updateParam.put("color", "bg-red-600");
        return updateParam;
    }

    public static ExtractableResponse<Response> 노선_삭제_요청(Long id) {
        return RestAssured.given().log().all()
                          .contentType(MediaType.APPLICATION_JSON_VALUE)
                          .when().delete("/lines/" + id)
                          .then().log().all()
                          .extract();
    }
}
