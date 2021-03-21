package nextstep.subway.path.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

public class PathSteps {

    public static ExtractableResponse<Response> 지하철_역간_최단경로_조회_요청(StationResponse source, StationResponse target) {
        return  RestAssured.given().log().all().
                get("/paths?source={sourceId}&target={targetId}", source.getId(), target.getId()).
                then().log().all().extract();
    }

    public static void 지하철_역간_최단경로_조회됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

}
