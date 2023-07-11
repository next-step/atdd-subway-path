package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.station.dto.StationRequest;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.stream.Collectors;

public class StationSteps {

    public static StationRequest 지하철생성요청_생성(String stationName) {
        return new StationRequest(stationName);
    }

    public static ExtractableResponse<Response> 지하철생성요청(StationRequest request) {
        return RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/stations")
                .then().log().all()
                .extract();
    }

    public static List<StationRequest> 지하철생성요청_다중생성(List<String> stationName) {
        return stationName.stream()
                .map(StationSteps::지하철생성요청_생성)
                .collect(Collectors.toList());
    }

    public static ExtractableResponse<Response> 지하철목록조회요청() {
        return RestAssured.given().log().all()
                .when().get("/stations")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철삭제요청(Long id) {
        return RestAssured.given().log().all()
                .when().delete("/stations/{id}", id)
                .then().log().all()
                .extract();
    }
}
