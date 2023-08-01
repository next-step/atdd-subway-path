package nextstep.subway.acceptance.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StationRequester {

    public static ExtractableResponse<Response> createStation(String stationName) {
        return createStationRequest(stationName);
    }

    public static Long createStationThenReturnId(String stationName) {
        return createStationRequest(stationName).jsonPath().getObject("id", Long.class);
    }

    public static List<String> findStationNames() {
        return findStationRequest().jsonPath().getList("name", String.class);
    }

    public static List<Long> findStationIds() {
        return findStationRequest().jsonPath().getList("id", Long.class);
    }

    public static ExtractableResponse<Response> deleteStation(Long stationId) {
        return deleteStationRequest(stationId);
    }

    private static ExtractableResponse<Response> createStationRequest(String stationName) {
        Map<String, String> params = new HashMap<>();
        params.put("name", stationName);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/stations")
                .then().log().all()
                .extract();
    }

    private static ExtractableResponse<Response> findStationRequest() {
        return RestAssured.given().log().all()
                .when().get("/stations")
                .then().log().all()
                .extract();
    }

    private static ExtractableResponse<Response> deleteStationRequest(Long stationId) {
        return RestAssured.given().log().all()
                .pathParam("id", stationId)
                .when().delete("/stations/{id}")
                .then().log().all().extract();
    }

}
