package nextstep.subway.acceptance.step;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Map;

public class PathStep {
    private PathStep() {
    }

    public static ExtractableResponse<Response> 출발_역에서_도착_역까지의_최단거리_조회(int sourceStationId, int targetStationId) {
        Map<String, Integer> params = Map.of(
                "source", sourceStationId,
                "target", targetStationId
        );

        return RestAssured.given().log().all()
                .queryParams(params)
                .when().get("/paths")
                .then().log().all()
                .extract();
    }
}
