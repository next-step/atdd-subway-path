package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.acceptance.LineSteps.*;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관리 기능")
class LineSectionAcceptanceTest extends AcceptanceTest {
    private Long 신분당선;

    private Long 강남역;
    private Long 양재역;

    /**
     * Given 지하철역과 노선 생성을 요청 하고
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        강남역 = 지하철역_생성_요청함("강남역");
        양재역 = 지하철역_생성_요청함("양재역");

        Map<String, String> lineCreateParams = createLineCreateParams(강남역, 양재역);
        신분당선 = 지하철_노선_생성_요청(lineCreateParams).jsonPath().getLong("id");
    }

    /**
     * When 지하철 노선에 새로운 구간 추가를 요청 하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @DisplayName("지하철 노선에 구간을 등록")
    @Test
    void addLineSection() {
        // when
        Long 정자역 = 지하철역_생성_요청함("정자역");
        지하철_노선에_지하철_구간_생성_요청함(신분당선, 양재역, 정자역);

        // then
        ExtractableResponse<Response> 신분당선_노선_정보 = 지하철_노선_조회_요청함(신분당선);
        지하철역이_순서에_따라_포함하고_있음(신분당선_노선_정보, 강남역, 양재역, 정자역);
    }

    /**
     * When 노선의 구간 사이에 새로운 구간을 추가하면
     * Then 구간 사이에 새로운 구간이 추가된다.
     */
    @DisplayName("구간 사이에 새로운 구간을 등록하면 구간이 추가된다.")
    @Test
    void addLineSection_between() {
        // when
        Long 정자역 = 지하철역_생성_요청함("정자역");
        지하철_노선에_지하철_구간_생성_요청함(신분당선, 강남역, 정자역);

        // then
        ExtractableResponse<Response> 신분당선_노선_정보 = 지하철_노선_조회_요청함(신분당선);
        지하철역이_순서에_따라_포함하고_있음(신분당선_노선_정보, 강남역, 정자역, 양재역);
    }

    /**
     * When 노선의 상행 종점역에 새로운 구간을 추가하면
     * Then 구간 상행에 새로운 구간이 추가된다.
     */
    @DisplayName("상행 종점에 새로운 구간을 등록하면 구간이 추가된다.")
    @Test
    void addLineSection_up() {
        // when
        Long 정자역 = 지하철역_생성_요청함("정자역");
        지하철_노선에_지하철_구간_생성_요청함(신분당선, 정자역, 강남역);

        // then
        ExtractableResponse<Response> 신분당선_노선_정보 = 지하철_노선_조회_요청함(신분당선);
        지하철역이_순서에_따라_포함하고_있음(신분당선_노선_정보, 정자역, 강남역, 양재역);
    }

    /**
     * When 노선의 하행 종점역에 새로운 구간을 추가하면
     * Then 구간 하행에 새로운 구간이 추가된다.
     */
    @DisplayName("하행 종점에 새로운 구간을 등록하면 구간이 추가된다.")
    @Test
    void addLineSection_down() {
        // when
        Long 정자역 = 지하철역_생성_요청함("정자역");
        지하철_노선에_지하철_구간_생성_요청함(신분당선, 양재역, 정자역);

        // then
        ExtractableResponse<Response> 신분당선_노선_정보 = 지하철_노선_조회_요청함(신분당선);
        지하철역이_순서에_따라_포함하고_있음(신분당선_노선_정보, 강남역, 양재역, 정자역);
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 지하철 노선의 첫번째 구간 제거를 요청 하면
     * Then 노선에 구간이 제거된다
     */
    @DisplayName("첫번째 구간을 삭제하면 해당 구간이 삭제된다.")
    @Test
    void removeLineSection() {
        // given
        Long 정자역 = 지하철역_생성_요청함("정자역");
        지하철_노선에_지하철_구간_생성_요청함(신분당선, 양재역, 정자역);

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 양재역);

        // then
        ExtractableResponse<Response> 신분당선_노선_정보 = 지하철_노선_조회_요청함(신분당선);
        지하철역을_찾을수없음(신분당선_노선_정보, 양재역);
    }

    private Long 지하철역_생성_요청함(String name) {
        return 지하철역_생성_요청(name).jsonPath().getLong("id");
    }

    private void 지하철_노선에_지하철_구간_생성_요청함(Long lineId, Long upStationId, Long downStationId) {
        지하철_노선에_지하철_구간_생성_요청(lineId, createSectionCreateParams(upStationId, downStationId));
    }

    private ExtractableResponse<Response> 지하철_노선_조회_요청함(Long lineId) {
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(lineId);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        return response;
    }

    private void 지하철역이_순서에_따라_포함하고_있음(ExtractableResponse<Response> lineResponse, Long... stationIds) {
        assertThat(lineResponse.jsonPath().getList("stations.id", Long.class)).containsExactly(stationIds);
    }

    private void 지하철역을_찾을수없음(ExtractableResponse<Response> lineResponse, Long... stationIds) {
        assertThat(lineResponse.jsonPath().getList("stations.id", Long.class)).doesNotContain(stationIds);
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

    private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", 6 + "");
        return params;
    }
}
