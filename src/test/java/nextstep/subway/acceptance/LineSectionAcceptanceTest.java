package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.acceptance.LineSteps.*;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_생성_요청;
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

        강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");

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
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 6));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        응답_확인(response, 강남역, 양재역, 정자역);
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 지하철 노선의 마지막 구간 제거를 요청 하면
     * Then 노선에 구간이 제거된다
     */
    @DisplayName("지하철 노선에 구간을 제거")
    @Test
    void removeLineSection() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 6));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 정자역);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        응답_확인(response, 강남역, 양재역);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 노선 중간에 신규 구간을 추가하면
     * Then 기존 노선이 둘로 생긴다.
     */
    @DisplayName("노선 중간 역 추가")
    @Test
    void createMiddleStationSection() {
        // given
        Long 뱅뱅사거리역 = 지하철역_생성_요청("뱅뱅사거리역").jsonPath().getLong("id");

        // when
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(뱅뱅사거리역, 양재역, 3));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        응답_확인(response, 강남역, 뱅뱅사거리역, 양재역);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 해당 구간보다 긴 노선을 중간에 신규 추가하면
     * Then Exception을 발생시킨다
     */
    @DisplayName("노선 중간에 새로 추가되는 구간이 기존 구간 이상의 거리를 가질 수 없음")
    @Test
    void createMiddleStationSectionException() {
        // given
        Long 뱅뱅사거리역 = 지하철역_생성_요청("뱅뱅사거리역").jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response =
            지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(뱅뱅사거리역, 양재역, 10));

        // then
        응답은_에러를_반환한다(response);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 상행역에 노선을 추가하면
     * Then 신규 노선이 추가된다.
     */
    @DisplayName("노선의 상행역에 노선을 추가할 수 있다.")
    @Test
    void createUpStationSection() {
        // given
        Long 신논현역 = 지하철역_생성_요청("신논현역").jsonPath().getLong("id");

        // when
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(신논현역, 강남역, 6));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        응답_확인(response, 신논현역, 강남역, 양재역);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 이미 등록된 역을 상,하행역으로 노선을 추가하면
     * Then 에러를 반환한다.
     */
    @DisplayName("노선에 이미 등록된 역들로 새로운 구간을 생성할 수 없다.")
    @Test
    void 이미_추가된_역들로_새로운_구간을_생성_할_수_없다() {
        // given
        // when
        ExtractableResponse<Response> response =
            지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 강남역, 5));

        // then
        응답은_에러를_반환한다(response);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 상, 하행역이 모두 등록되지 않은 노선을 추가하면
     * Then 에러를 반환한다.
     */
    @DisplayName("노선에 새로 추가되는 구간의 역이 하나도 등록되지 않은 경우, 에러를 반환한다")
    @Test
    void 노선에_상하행선_모두_등록되지_않은_경우() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        Long 미금역 = 지하철역_생성_요청("미금역").jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response =
            지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(정자역, 미금역, 5));

        // then
        응답은_에러를_반환한다(response);
    }

    private void 응답은_에러를_반환한다(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private void 응답_확인(ExtractableResponse<Response> response, Long... stations) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactlyElementsOf(List.of(stations));
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

    private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId, Integer distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", distance + "");
        return params;
    }
}
