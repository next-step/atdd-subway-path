package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static nextstep.subway.acceptance.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.PathSteps.지하철역_경로_조회_요청;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("경로조회 기능 ")
public class PathAcceptanceTest extends AcceptanceTest {

    private Long 신분당선;

    private Long 강남역;
    private Long 양재역;
    private Long 양재시민의숲역;
    @BeforeEach
    void setup() {
        super.setUp();
        강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");
        양재시민의숲역 = 지하철역_생성_요청("양재시민의숲역").jsonPath().get("id");

        Map<String, String> lineCreateParams = createLineCreateParams(강남역, 양재역);
        신분당선 = 지하철_노선_생성_요청(lineCreateParams).jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 양재시민의숲역));
    }

    /**
     * Given 강남역 - 양재역 - 양재 시민의 숲역이 주어질때
     * When 강남역에서 - 양재시민의 숲역 경로를 조회하면
     * Then 출발역부터 도착역까지의 경로에 있는 역 목록과 경로 구간의 거리를 구할 수 있다.
     */
    @DisplayName("출발역과 도착역을 통해 경로를 조회할 수 있다.")
    @Test
    void pathTest() {
        ExtractableResponse<Response> response = 지하철역_경로_조회_요청();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.contentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);

        List<String> names = response.jsonPath().getList("stations.name", String.class);

        assertThat(names).containsExactly("강남역", "양재역", "양재시민의숲역");
        assertThat(response.jsonPath().getInt("distance")).isEqualTo(16);
    }

    private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", 6 + "");
        return params;
    }

    private Map<String, String> createLineCreateParams(Long upStationId, Long downStationId) {
        Map<String, String> lineCreateParams;
        lineCreateParams = new HashMap<>();
        lineCreateParams.put("name", "신분당선");
        lineCreateParams.put("color", "bg-red-600");
        lineCreateParams.put("upStationId", upStationId + "");
        lineCreateParams.put("downStationId", downStationId + "");
        lineCreateParams.put("distance", 10 + "");
        return lineCreateParams;
    }
}
