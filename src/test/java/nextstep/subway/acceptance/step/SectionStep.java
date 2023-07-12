package nextstep.subway.acceptance.step;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Map;
import org.springframework.http.MediaType;

public class SectionStep {
    private SectionStep() {
    }

    public static ExtractableResponse<Response> 지하철_노선_구간을_등록한다(long lineId, long upStationId, long downStationId) {
        Map<String, ? extends Number> params = Map.of(
                "upStationId", upStationId,
                "downStationId", downStationId,
                "distance", 10
        );

        return RestAssured.given().log().all()
                .pathParam("lineId", lineId)
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines/{lineId}/sections")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_구간을_삭제한다(long lineId, long stationId) {
        return RestAssured.given().log().all()
                .pathParam("lineId", lineId)
                .queryParam("stationId", stationId)
                .when().delete("/lines/{lineId}/sections")
                .then().log().all()
                .extract();
    }
}
