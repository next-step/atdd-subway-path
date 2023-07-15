package nextstep.subway.utils;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import org.springframework.http.MediaType;
import nextstep.subway.line.controller.dto.LineResponse;

public class LineTestRequests {
    public static ExtractableResponse<Response> 지하철_노선도_등록(String name, String color, Long upStationId, Long downStationId, Integer distance) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);
        return RestAssured.given().log().all()
                .body(params).contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_수정(Long id, String name, String color) {
        String pathVariable = "/" + id;
        HashMap<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        return RestAssured.given().log().all()
                .body(params).contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/lines" + pathVariable)
                .then().log().all()
                .extract();
    }

    public static List<LineResponse> 지하철_노선_목록_조회() {
        return RestAssured.given().log().all()
                .when().get("/lines")
                .then().log().all()
                .extract()
                .jsonPath()
                .getList("", LineResponse.class);
    }

    public static ExtractableResponse<Response> 지하철_노선_삭제(Long id) {
        String pathVariable = "/" + id;
        return RestAssured.given().log().all()
                .when().delete("/lines" + pathVariable)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_조회(Long id) {
        String pathVariable = "/" + id;
        return RestAssured.given().log().all()
                .when().get("/lines" + pathVariable)
                .then().log().all()
                .extract();
    }

    public static LineResponse 지하철_노선_조회_응답값_반환(Long id) {
        return 지하철_노선_조회(id).jsonPath().getObject("", LineResponse.class);
    }
}
