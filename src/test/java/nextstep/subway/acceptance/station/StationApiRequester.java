package nextstep.subway.acceptance.station;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class StationApiRequester {

    public static ExtractableResponse<Response> createStationApiCall(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);

        return given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/stations")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> showStationsApiCall() {
        return given().log().all()
                .when().get("/stations")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> deleteStationApiCall(Long id) {
        return given().log().all()
                .pathParam("id", id)
                .when().delete("/stations/{id}")
                .then().log().all()
                .extract();
    }
}
