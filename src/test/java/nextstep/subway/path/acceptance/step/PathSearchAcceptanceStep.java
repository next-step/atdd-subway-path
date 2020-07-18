package nextstep.subway.path.acceptance.step;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.path.ui.FindType;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

public class PathSearchAcceptanceStep {

    /**
     * @param src source
     * @param dst destination
     * @return
     */
    public static ExtractableResponse<Response> 출발역에서_도착역까지의_최단_거리_경로_조회를_요청(Long src, Long dst) {
        return 최단_경로_요청(src, dst, FindType.DISTANCE);
    }

    public static ExtractableResponse<Response> 출발역에서_도착역까지의_최단_시간_경로_조회를_요청(Long src, Long dst) {
        return 최단_경로_요청(src, dst, FindType.DURATION);
    }

    private static ExtractableResponse<Response> 최단_경로_요청(Long src, Long dst, FindType type) {
        return RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .get("/paths?source={src}&target={dst}&type={type}", src, dst, type)
                .then()
                .log().all()
                .extract();
    }

    public static void 최단_거리_경로를_응답(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 총_거리와_소요_시간을_함께_응답함(ExtractableResponse<Response> response) {
        final PathResponse pathResponse = response.as(PathResponse.class);
        assertThat(pathResponse).isNotNull();
        assertThat(pathResponse.getDistance()).isNotNull();
        assertThat(pathResponse.getDuration()).isNotNull();
    }
}
