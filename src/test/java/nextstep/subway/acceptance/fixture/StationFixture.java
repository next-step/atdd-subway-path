package nextstep.subway.acceptance.fixture;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.station.StationRequest;
import org.springframework.http.MediaType;

public class StationFixture {

    public static ExtractableResponse<Response> newStation() {
        return newStation("강남역");
    }

    public static Long newStationAndGetId(String stationName) {
        return newStation(stationName)
                .jsonPath()
                .getLong("id");
    }

    public static ExtractableResponse<Response> newStation(String stationName) {
        StationRequest request = new StationRequest(stationName);
        return RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/stations")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> loadStations() {
        return RestAssured.given().log().all()
                .when().get("/stations")
                .then().log().all()
                .extract();
    }
}
