package nextstep.subway.fixture;

import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import nextstep.subway.station.presentation.StationResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StationSteps {

    public static StationResponse createStation(String stationName) {
        Map<String, String> param = new HashMap<>();
        param.put("name", stationName);

        return RestAssured.given().log().all()
                .body(param)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(StationResponse.class);
    }


    public static List<StationResponse> getStations() {
        return RestAssured.given().log().all()
                .when()
                .get("stations")
                .then()
                .log().all()
                .extract()
                .as(new TypeRef<>() {
                });
    }


    public static StationResponse getStation(long id) {
        return RestAssured.given().log().all()
                .pathParam("id", id)
                .when()
                .get("stations/{id}")
                .then()
                .log().all()
                .extract()
                .as(StationResponse.class);
    }
}
