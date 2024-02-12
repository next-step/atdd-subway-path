package nextstep.subway.common;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

public class StationRestAssuredCRUD {

    public static ExtractableResponse<Response> createStation(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);

        return RestAssured
                .given().log().all()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(params)
                .when()
                    .post("/stations")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> showStations() {
        return RestAssured
                .given().log().all()
                .when()
                    .get("/stations")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> deleteStation(Long deleteId) {
        return RestAssured
                .given().log().all()
                    .pathParam("id", deleteId)
                .when()
                    .delete("/stations/{id}")
                .then().log().all()
                .extract();
    }

}
