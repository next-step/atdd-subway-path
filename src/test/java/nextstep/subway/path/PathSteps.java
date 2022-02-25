package nextstep.subway.path;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import nextstep.subway.line.acceptance.LineSteps;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.StationSteps;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.http.MediaType;

public class PathSteps {
    public static StationResponse 지하철역_생성_요청(String name) {
        return StationSteps.지하철역_생성_요청(name).as(StationResponse.class);
    }

    public static LineResponse 지하철_노선_생성_요청(
            String lineName,
            String color,
            StationResponse upStation,
            StationResponse downStation,
            int distance
    ) {
        Map<String, String> params = new HashMap<>();
        params.put("name", lineName);
        params.put("color", color);
        params.put("upStationId", upStation.getId() + "");
        params.put("downStationId", downStation.getId() + "");
        params.put("distance", distance + "");

        return LineSteps.지하철_노선_생성_요청(params).as(LineResponse.class);
    }

    public static void 지하철_노선에_지하철_구간_생성_요청(LineResponse line, StationResponse upStation, StationResponse downStation, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStation.getId() + "");
        params.put("downStationId", downStation.getId() + "");
        params.put("distance", distance + "");

        RestAssured.given().log().all().
                contentType(MediaType.APPLICATION_JSON_VALUE).
                body(params).
                when().
                post("/lines/{lineId}/sections", line.getId()).
                then().
                log().all().
                extract();
    }

    public static ExtractableResponse<Response> 지하철_최단경로_조회_요청(
            StationResponse source, StationResponse target) {
        return RestAssured
                .given().log().all()
                .queryParam("source", source.getId())
                .queryParam("target", target.getId())
                .when().get("/paths")
                .then().log().all().extract();
    }
}
