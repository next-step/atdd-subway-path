package nextstep.subway.acceptance.line;

import static nextstep.subway.acceptance.common.SubwayUtils.responseToId;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class LineUtils {

    public static final String 신분당선 = "신분당선";
    public static final String 일호선 = "일호선";
    public static final String 이호선 = "이호선";
    public static final String 삼호선 = "삼호선";

    public static ExtractableResponse<Response> 지하철노선_생성(String name, String color,
        Long upStationId, Long downStationId, Long distance) {
        Map<String, Object> params = createParams(name, color, upStationId, downStationId,
            distance);

        ExtractableResponse<Response> response =
            RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract();

        return response;
    }

    public static ExtractableResponse<Response> 지하철노선_생성_후_검증(String name, String color,
        Long upStationId, Long downStationId, Long distance) {
        ExtractableResponse<Response> 지하철노선_생성_응답 = 지하철노선_생성(name, color, upStationId,
            downStationId, distance);
        assertThat(지하철노선_생성_응답.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        return 지하철노선_생성_응답;
    }

    public static Long 지하철노선_생성_후_ID_반환(String name, String color, Long upStationId,
        Long downStationId, Long distance) {
        return responseToId(지하철노선_생성_후_검증(name, color, upStationId, downStationId, distance));
    }

    public static ExtractableResponse<Response> 지하철노선_목록조회() {
        return RestAssured.given().log().all()
            .when().get("/lines")
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 지하철노선_조회(Long id) {
        return RestAssured.given().log().all()
            .when().get("/lines/" + id)
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 지하철노선_수정(Long id, String name, String color) {
        Map<String, Object> params = updateParams(name, color);

        return RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().put("/lines/" + id)
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 지하철노선_삭제(String location) {
        return RestAssured.given().log().all()
            .when().delete(location)
            .then().log().all()
            .extract();
    }

    public static List<String> responseToStationNames(ExtractableResponse<Response> response) {
        return response.jsonPath().getList("stations.name", String.class);
    }

    private static Map<String, Object> createParams(String name, String color, Long upStationId,
        Long downStationId, Long distance) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);
        return params;
    }

    private static Map<String, Object> updateParams(String name, String color) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        return params;
    }

}
