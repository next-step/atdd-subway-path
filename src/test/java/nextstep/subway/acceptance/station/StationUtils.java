package nextstep.subway.acceptance.station;

import static nextstep.subway.acceptance.common.SubwayUtils.responseToId;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class StationUtils {

    public static final String 신사역 = "신사역";
    public static final String 논현역 = "논현역";
    public static final String 신논현역 = "신논현역";
    public static final String 강남역 = "강남역";
    public static final String 역삼역 = "역삼역";
    public static final String 교대역 = "교대역";
    public static final String 양재역 = "양재역";
    public static final String 남부터미널역 = "남부터미널역";


    public static ExtractableResponse<Response> 지하철역_생성(String stationName) {
        Map<String, String> params = createParams(stationName);

        return RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/stations")
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 지하철역_목록조회() {
        return RestAssured.given().log().all()
            .when().get("/stations")
            .then().log().all()
            .extract();
    }

    public static void 지하철역_삭제(String location) {
        RestAssured.given().log().all()
            .when().delete(location)
            .then().log().all()
            .statusCode(HttpStatus.NO_CONTENT.value());
    }

    public static Long 지하철역_생성_후_id_추출(String name) {
        ExtractableResponse<Response> 지하철역_생성_응답 = 지하철역_생성(name);
        assertThat(지하철역_생성_응답.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        return responseToId(지하철역_생성_응답);
    }

    private static Map<String, String> createParams(String stationName) {
        Map<String, String> params = new HashMap<>();
        params.put("name", stationName);
        return params;
    }
}
