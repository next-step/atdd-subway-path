package nextstep.subway.steps;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StationSteps {

    public static Long createStation(String stationName) {
        Map<String, String> params = new HashMap<>();
        params.put("name", stationName);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/stations")
                .then().log().all()
                .extract()
                .response().jsonPath().getLong("id");
    }

    public static List<String> getStations() {
        List<String> stationNames = RestAssured.given().log().all()
                .when().get("/stations")
                .then().log().all()
                .extract()
                .response().jsonPath().getList("name", String.class);
        return stationNames;
    }

    public static Long getStationId(ExtractableResponse<Response> station) {
        return station.response().jsonPath().getLong("id");
    }

    public static Response deleteStation(String id) {
        return RestAssured.given().log().all()
                .when().delete("/stations/" + id)
                .then().log().all()
                .extract()
                .response();
    }
}
