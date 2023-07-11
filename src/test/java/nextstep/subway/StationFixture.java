package nextstep.subway;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.station.view.StationResponse;

public class StationFixture {
    private static final String CREATE_STATION_PATH = "/stations";

    public StationResponse 지하철역_생성(String stationName) {
        Map<String, String> params = new HashMap<>();
        params.put("name", stationName);

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                                                         .body(params)
                                                         .contentType(MediaType.APPLICATION_JSON_VALUE)
                                                         .when().post(CREATE_STATION_PATH)
                                                         .then().log().all()
                                                         .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        return response.as(StationResponse.class);
    }

    private Long extractCreateStationId(ExtractableResponse<Response> response) {
        return Long.valueOf(response.header(HttpHeaders.LOCATION).split("/")[2]);
    }
}
