package nextstep.subway.acceptance.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.station.presentation.request.CreateStationRequest;
import org.springframework.http.MediaType;

public class StationAcceptanceStep {

    public static ExtractableResponse<Response> 지하철_역_생성(CreateStationRequest createStationRequest) {
        return RestAssured
                .given().log().all()
                .body(createStationRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/stations")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_역_목록_조회() {
        return RestAssured
                .given().log().all()
                .when().get("/stations")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 노선_삭제(Long stationId) {
        return RestAssured
                .given().log().all()
                .pathParam("stationId", stationId)
                .when().delete("/stations/{stationId}")
                .then().log().all()
                .extract();
    }

}
