package nextstep.subway.steps;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.PathRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;

import static io.restassured.RestAssured.given;

public class PathSteps {

    public static ExtractableResponse<Response> 성공하는_지하철_경로_조회_요청(PathRequest pathRequest) {
        return given()
                .param("source", pathRequest.getDepartureStationId())
                .param("target", pathRequest.getArrivalStationId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/paths")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract();
    }

    public static void 실패하는_지하철_경로_조회_요청(PathRequest pathRequest) {
        given()
                .param("source", pathRequest.getDepartureStationId())
                .param("target", pathRequest.getArrivalStationId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/paths")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract();
    }

    public static int convertToDistance(ExtractableResponse<Response> 성공하는_경로_조회_응답) {
        return 성공하는_경로_조회_응답.jsonPath().getInt("distance");
    }

    public static List<Long> convertToStationIds(ExtractableResponse<Response> 성공하는_경로_조회_응답) {
        return 성공하는_경로_조회_응답.jsonPath().getList("stations.id", Long.class);
    }
}
