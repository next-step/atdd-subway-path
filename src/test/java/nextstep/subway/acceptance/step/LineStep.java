package nextstep.subway.acceptance.step;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Map;
import org.springframework.http.MediaType;

public class LineStep {
    private LineStep() {
    }

    public static ExtractableResponse<Response> 지하철_노선을_생성한다(String upStationName, String downStationName, String lineName) {
        ExtractableResponse<Response> responseOfUpStation = StationStep.지하철역을_생성한다(upStationName);
        ExtractableResponse<Response> responseOfDownStation = StationStep.지하철역을_생성한다(downStationName);

        Map<String, Object> params = Map.of(
                "name", lineName,
                "color", "bg-red-600",
                "upStationId", extractId(responseOfUpStation),
                "downStationId", extractId(responseOfDownStation),
                "distance", 10
        );

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선을_생성한다(Long upStationId, Long downStationId, String lineName, int distance) {
        Map<String, Object> params = Map.of(
                "name", lineName,
                "color", "bg-red-600",
                "upStationId", upStationId,
                "downStationId", downStationId,
                "distance", distance
        );

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract();
    }

    private static long extractId(ExtractableResponse<Response> responseOfCreateStation) {
        return responseOfCreateStation.jsonPath().getLong("id");
    }

    public static ExtractableResponse<Response> 지하철_노선을_조회한다(long lineId) {
        return RestAssured.given().log().all()
                .pathParam("id", lineId)
                .when().get("/lines/{id}")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_목록을_조회한다() {
        return RestAssured.given().log().all()
                .when().get("/lines")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선을_수정한다(long lineId, String lineName, String color) {
        Map<String, String> params = Map.of(
                "name", lineName,
                "color", color
        );

        return RestAssured.given().log().all()
                .body(params)
                .pathParam("id", lineId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/lines/{id}")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선을_삭제한다(long lineId) {
        return RestAssured.given().log().all()
                .pathParam("id", lineId)
                .when().delete("/lines/{id}")
                .then().log().all()
                .extract();
    }
}
