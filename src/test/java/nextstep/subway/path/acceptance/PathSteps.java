package nextstep.subway.path.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.station.dto.StationResponse;

import java.util.List;
import java.util.Map;

public class PathSteps {

    public static ExtractableResponse<Response> 최단거리_조회_요청(Map<String, String> params) {
        return RestAssured
                .given().log().all().queryParams(params)
                .when().get("/paths")
                .then().log().all().extract();
    }

    public static void 최단거리_지하철역_순서_정렬됨(ExtractableResponse<Response> response, List<StationResponse> expectedStations) {

    }
}
