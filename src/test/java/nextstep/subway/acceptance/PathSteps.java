package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class PathSteps {
    public static ExtractableResponse<Response> 최단_거리_경로_검색_요청(Long source, Long target) {
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/paths?source={source}&target={target}", source, target)
                .then().log().all().extract();
    }
    public static void 최단_거리_경로_검색_완료(ExtractableResponse<Response> response, Long[] ids) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id"))
                .containsExactly(Arrays.stream(ids).map(Long::intValue).toArray());
    }

    public static void 최단_거리_경로_검색_실패(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
