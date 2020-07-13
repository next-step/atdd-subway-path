package nextstep.subway.map.acceptance.step;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

public class MapAcceptanceStep {

    public static ExtractableResponse<Response> 지하철_노선도_조회_요청() {
        return null;
    }

    public static void 지하철_노선도_조회_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 지하철_노선도에_노선별_지하철역_순서_정렬됨(ExtractableResponse<Response> response, Long lineId, Long... stationIds) {

    }
}
