package nextstep.subway.path.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.http.MediaType;

import java.util.Map;

public class PathSteps {

    public static ExtractableResponse<Response> 지하철_경로_조회_요청(StationResponse source, StationResponse target) {
        return RestAssured.given().log().all()
                .queryParam("source", source.getId())
                .queryParam("target", target.getId())
                .when().get("/path")
                .then().log().all().extract();
    }

}
