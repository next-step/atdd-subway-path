package nextstep.subway.line.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

public class SectionSteps {

    public static ExtractableResponse<Response> 지하철_노선에_구간_등록(long lineId, Map<String, String> sectionRequest) {

        return RestAssured.given().log().all().
                contentType(MediaType.APPLICATION_JSON_VALUE).
                body(sectionRequest).
                when().
                post("/lines/{lineId}/sections", lineId).
                then().
                log().all().
                extract();
    }

    public static ExtractableResponse<Response> 지하철_노선에서_구간_제거(long lineId, long removeStationId) {
        return RestAssured.given().log().all().
                when().
                delete("/lines/{lineId}/sections?stationId={stationId}", lineId, removeStationId).
                then().
                log().all().
                extract();
    }

    public static ExtractableResponse<Response> 지하철_노선의_마지막역_조회(long lineId) {
        return RestAssured.given().log().all().
                when().
                get("/lines/{lineId}/sections/last-station", lineId).
                then().
                log().all().
                extract();
    }

}
