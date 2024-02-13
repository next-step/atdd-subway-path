package nextstep.subway.fixture;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import java.util.HashMap;

public class LineTestFixture {

    public static ExtractableResponse<Response> createLine(String lineName, String lineColor, long upStationId, long downStationId) {
        return RestAssured.given().log().all()
                .body(new HashMap<String, String>() {{
                    put("name", lineName);
                    put("color", lineColor);
                    put("upStationId", String.valueOf(upStationId));
                    put("downStationId", String.valueOf(downStationId));
                }})
                .contentType("application/json")
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> createLine(String lineName, String lineColor) {
        return RestAssured.given().log().all()
                .body(new HashMap<String, Object>() {{
                    put("name", lineName);
                    put("color", lineColor);
                }})
                .contentType("application/json")
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> allLines() {
        return RestAssured.given().log().all()
                .when().get("/lines")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> getLine(long id) {
        return RestAssured.given().log().all()
                .when().get("/lines/{id}", id)
                .then()
                .extract();
    }

    public static ExtractableResponse<Response> showLine(long id) {
        return RestAssured.given().log().all()
                .when().get("/lines/{id}",id)
                .then().log().all()
                .extract();
    }

    public static void updateLine(long id, HashMap<String, String> params) {
        RestAssured.given().log().all()
            .body(params)
            .contentType("application/json")
            .when().put("/lines/{id}",id)
            .then().log().all();
    }

    public static void deleteLine(long id) {
        RestAssured.given().log().all()
            .when().delete("/lines/{id}", id)
            .then().log().all();
    }
}
