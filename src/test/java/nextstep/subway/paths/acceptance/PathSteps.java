package nextstep.subway.paths.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.http.MediaType;


public class PathSteps {

    public static ExtractableResponse< Response > 최단거리_조회_요청(StationResponse sourceStation, StationResponse targetStation) {
        return RestAssured.given().log().all().
                    contentType(MediaType.APPLICATION_JSON_VALUE).
                when().
                    queryParam("source", sourceStation.getId()).
                    queryParam("target", targetStation.getId()).
                    get("/paths").
                then().
                    log().all().
                    extract();
    }
}


