package nextstep.subway.testhelper;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.path.PathRequest;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.given;

public class PathApiCaller {

    public static ExtractableResponse<Response> 경로_조회(PathRequest pathRequest) {
        return given().log().all()
                .queryParam("source", pathRequest.getSource())
                .queryParam("target", pathRequest.getTarget())
                .when().get("/paths")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
    }
}
