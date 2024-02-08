package nextstep.subway.utils;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.LineResponse;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

public class LineFactory {

    public static LineResponse createLineWithDistance(String name, Long upstationId, Long downstationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("upstationId", upstationId.toString());
        params.put("downstationId", downstationId.toString());
        params.put("color", "red");
        params.put("distance", Integer.toString(distance));

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract().as(LineResponse.class);
    }

    public static LineResponse createLine(String name, Long upstationId, Long downstationId) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("upstationId", upstationId.toString());
        params.put("downstationId", downstationId.toString());
        params.put("color", "red");
        params.put("distance", "10");

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract().as(LineResponse.class);
    }

    public static ExtractableResponse<Response> getLine(Long id) {
        return RestAssured.given().log().all()
                .pathParam("id", id)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines/{id}")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> getLines() {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines")
                .then().log().all()
                .extract();
    }
}
