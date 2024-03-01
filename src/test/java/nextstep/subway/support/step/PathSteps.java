package nextstep.subway.support.step;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.springframework.http.MediaType;

public class PathSteps {

    public static final String SUBWAY_PATH_BASE_PATH = "/paths";

    public static ExtractableResponse<Response> 지하철_경로_조회_요청(Long sourceStationId, Long targetStationId) {
        return RestAssured.given().log().all()
            .queryParam("source", sourceStationId)
            .queryParam("target", targetStationId)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().get(SUBWAY_PATH_BASE_PATH)
            .then().log().all()
            .extract();
    }

    public static List<Long> 지하철역_경로_조회_응답에서_역_아이디_목록_추출(ExtractableResponse<Response> 지하철_경로_조회_응답) {
        return 지하철_경로_조회_응답.jsonPath()
            .getList("id", Long.class);
    }
}
