package nextstep.subway.acceptance.commonStep;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

public class PathStep {

    public static ExtractableResponse<Response> 지하철_경로_조회(Long sourceStationId,Long targetStationId){

        ExtractableResponse<Response> response =
                RestAssured.given()
                        .when()
                        .get("/paths?source=" + sourceStationId + "&target=" + targetStationId)
                        .then().log().all()
                        .extract();

        return response;
    }
}
