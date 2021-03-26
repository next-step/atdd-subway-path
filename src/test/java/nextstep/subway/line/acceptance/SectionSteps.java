package nextstep.subway.line.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.http.MediaType;

import java.sql.Statement;
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

    public static ExtractableResponse<Response> 지하철_노선에_지하철역_등록되어_있음(LineResponse line, StationResponse upStation, StationResponse downStation, Integer distance){

        Map<String, String> sectionCreateParams = new HashMap<>();
        sectionCreateParams.put("upStationId", java.lang.String.valueOf(upStation.getId()));
        sectionCreateParams.put("downStationId", java.lang.String.valueOf(downStation.getId()));
        sectionCreateParams.put("distance", java.lang.String.valueOf(distance));

        return RestAssured.given().log().all().
                contentType(MediaType.APPLICATION_JSON_VALUE).
                body(sectionCreateParams).
                when().
                post("/lines/{lineId}/sections", line.getId()).
                then().
                log().all().
                extract();
    }

}
