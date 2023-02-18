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
    private Long 판교역;

    /**
     * Given 지하철역과 노선 생성을 요청 하고
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        판교역 = 지하철역_생성_요청("판교역").jsonPath().getLong("id");

        Map<String, String> lineCreateParams = createLineCreateParams(강남역, 판교역, 10);
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
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(판교역, 정자역, 3));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 판교역, 정자역);
    }

    /**
     * Given 새로운 구간을 위한 역을 추가하고
     * When 지하철 노선에 새로운 구간 추가를 요청하면 (기존 구간의 역 사이에 추가)
     * Then 노선에 새로운 구간이 추가된다.
     */
    @DisplayName("지하철 노선의 역 사이에 새로운 역을 등록")
    @Test
    void addLineSectionWithinStation() {
        // given
        Long 양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");
        Long 청계산입구역 = 지하철역_생성_요청("청계산입구역").jsonPath().getLong("id");

        // when
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 양재역, 4));
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(청계산입구역, 판교역, 3));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역, 청계산입구역, 판교역);
    }

    /**
     * Given 새로운 구간을 위한 역을 추가하고
     * When 지하철 노선에 새로운 구간 추가를 요청하면 (새로운 역을 상행 종점역에 등록하는 경우)
     * Then 노선에 새로운 구간이 추가된다.
     */
    @DisplayName("지하철 노선의 새로운 상행 종점역 구간을 등록")
    @Test
    void addLineSectionBeforeFirstStation() {
        // given
        Long 신논현역 = 지하철역_생성_요청("신논현역").jsonPath().getLong("id");

        // when
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(신논현역, 강남역, 2));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(신논현역, 강남역, 판교역);
    }

    /**
     * Given 새로운 구간을 위한 역을 추가하고
     * When 지하철 노선에 새로운 구간 추가를 요청하면 (새로운 역을 하행 종점역에 등록하는 경우)
     * Then 노선에 새로운 구간이 추가된다.
     */
    @DisplayName("지하철 노선의 새로운 하행 종점역 구간을 등록")
    @Test
    void addLineSectionAfterFinalStation() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");

        // when
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(판교역, 정자역, 2));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 판교역, 정자역);
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
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(판교역, 정자역, 3));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 정자역);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 판교역);
    }

    /**
     * Given 지하철 노선에 새로운 두 구간 추가를 요청 하고
     * When 지하철 노선의 두 구간 중, 중간 역을 제거하면
     * Then 노선에 구간이 제거된다
     */
    @DisplayName("지하철 노선에 등록된 역들 중, 중간 역을 제거한다.")
    @Test
    void removeLineSectionWithinStation() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(판교역, 정자역, 3));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 판교역);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 정자역);
    }

    private Map<String, String> createLineCreateParams(Long upStationId, Long downStationId, int distance) {
        Map<String, String> lineCreateParams;
        lineCreateParams = new HashMap<>();
        lineCreateParams.put("name", "신분당선");
        lineCreateParams.put("color", "bg-red-600");
        lineCreateParams.put("upStationId", upStationId + "");
        lineCreateParams.put("downStationId", downStationId + "");
        lineCreateParams.put("distance", distance + "");
        return lineCreateParams;
    }

    private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", distance + "");
        return params;
    }
}
