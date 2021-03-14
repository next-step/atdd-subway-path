package nextstep.subway.path.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.utils.Extractor;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

public class PathSteps {
    public static final String PATH = "/paths";

    public static ExtractableResponse<Response> 지하철_노선에_지하철역_최단_경로_요청(Long source, Long target) {
        String endPoint = PATH + String.format("?source=%d&target=%d", source, target);
        return Extractor.get(endPoint);
    }

    public static void 지하철_노선에_지하철역_최단_경로_요청_실패됨(ExtractableResponse<Response> response1) {
        assertThat(response1.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    public static void 에러_응답_메세지_비교(ExtractableResponse<Response> response, String message) {
        assertThat(response.asString()).isEqualTo(message);
    }
}
