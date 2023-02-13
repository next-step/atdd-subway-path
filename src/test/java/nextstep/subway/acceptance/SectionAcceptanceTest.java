package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.acceptance.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선_조회_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_제거_요청;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관리 기능")
class SectionAcceptanceTest extends AcceptanceTest {
    private Long 신분당선;

    private Long 강남역;
    private Long 양재역;

    private Long 정자역;

    /**
     * Given 지하철역과 노선 생성을 요청 하고
     *
     * 강남역 <---10---> 양재역
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");

        Map<String, String> lineCreateParams = createLineCreateParams(강남역, 양재역);
        신분당선 = 지하철_노선_생성_요청(lineCreateParams).jsonPath().getLong("id");

        정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
    }

    /**
     * Given 지하철 노선의 하행 종착역이, 상행역인 구간을
     * When 지하철 노선에 새로운 구간 추가를 요청 하면
     * Then 노선에 새로운 구간이 추가된다
     *
     * As-is
     *                   양재역 <----6----> 정자역
     *                                     ↓
     * 강남역 <----10----> 양재역
     *
     * To-be
     * 강남역 <----10----> 양재역 <----6----> 정자역
     */
    @DisplayName("지하철 노선에 새로운 꼬리 구간을 등록")
    @Test
    void extendSection() {
        // Given
        Map<String, String> section = createSectionCreateParams(양재역, 정자역);

        // When
        지하철_노선에_지하철_구간_생성_요청(신분당선, section);

        // Then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역, 정자역);
    }

    /**
     * Given 지하철 노선의 상행 종착역이, 상행역인 구간을
     * When 지하철 노선에 새로운 구간 추가를 요청 하면
     * Then 노선에 새로운 구간이 추가된다
     *
     * As-is
     * 강남역 <----6----> 정자역
     *                    ↓
     * 강남역 <---------10---------> 양재역
     *
     * To-be
     * 강남역 <----6----> 정자역 <-4-> 양재역
     */
    @DisplayName("지하철 노선에 새로운 사이 구간을 상행역 기준으로 등록")
    @Test
    void betweenExtendSectionByUpStation() {
        // Given
        Map<String, String> section = createSectionCreateParams(강남역, 정자역);

        // When
        지하철_노선에_지하철_구간_생성_요청(신분당선, section);

        // Then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 정자역, 양재역);
    }

    /**
     * Given 지하철 노선의 하행 종착역이, 하행역인 구간을
     * When 지하철 노선에 새로운 구간 추가를 요청 하면
     * Then 노선에 새로운 구간이 추가된다
     *
     * As-is
     *            정자역 <----6----> 양재역
     *              ↓
     * 강남역 <---------10---------> 양재역
     *
     * To-be
     * 강남역 <-4-> 정자역 <----6----> 양재역
     */
    @DisplayName("지하철 노선에 새로운 꼬리 구간을 하행역 기준으로 등록")
    @Test
    void betweenExtendSectionByDownStation() {
        // Given
        Map<String, String> section = createSectionCreateParams(정자역, 양재역);

        // When
        지하철_노선에_지하철_구간_생성_요청(신분당선, section);

        // Then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 정자역, 양재역);
    }

    /**
     * Given 기존 구간과 같은 길이의 구간을
     * When 지하철 노선에 새로운 구간 추가를 요청 하면
     * Then 등록되지 않는다
     *
     * 강남역 <--------10-------> 정자역
     *                           ↓
     *                           X
     * 강남역 <--------10-------> 양재역
     */
    @DisplayName("기존 구간과 같은 길이 구간은 등록 불가")
    @Test
    void exceptionWhenConflictDistance() {
        // Given
        Map<String, String> section = createSectionCreateParamsWithDistance(강남역, 정자역, 10);

        // When
        ExtractableResponse<Response> responseOf구간생성 = 지하철_노선에_지하철_구간_생성_요청(신분당선, section);

        // Then
        assertThat(responseOf구간생성.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    /**
     * Given 기존 구간보다 큰 길이의 구간을
     * When 지하철 노선에 새로운 구간 추가를 요청 하면
     * Then 등록되지 않는다
     *
     * 강남역 <------------12-----------> 정자역
     *                                    ↓
     *                                    X
     * 강남역 <--------10-------> 양재역
     */
    @DisplayName("기존 구간보다 긴 길이 구간은 등록 불가")
    @Test
    void exceptionWhenExceededDistance() {
        // Given
        Map<String, String> section = createSectionCreateParamsWithDistance(강남역, 정자역, 12);

        // When & Then
        ExtractableResponse<Response> responseOf구간생성 = 지하철_노선에_지하철_구간_생성_요청(신분당선, section);

        // Then
        assertThat(responseOf구간생성.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    /**
     * Given 노선에 이미 등록되어있는 역의 구간을
     * When 지하철 노선에 새로운 구간 추가를 요청 하면
     * Then 등록되지 않는다
     *
     * 강남역 <--------10-------> 양재역
     *   ↓                        ↓
     *   X                        X
     * 강남역 <--------10-------> 양재역
     */
    @DisplayName("노선에 모두 존재하는 역들로 이루어진 구간은 등록 불가")
    @Test
    void exceptionWhenRequestStationsAreAlreadyResister() {
        // Given
        Map<String, String> section = createSectionCreateParamsWithDistance(강남역, 양재역, 10);

        // When
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, section);

        // Then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    /**
     * Given 노선의 상행 종착역이, 하행역인 구간을
     * When 지하철 노선에 새로운 구간 추가를 요청 하면
     * Then 노선에 새로운 구간이 추가된다
     *
     * As-is
     * 정자역 <--6--> 강남역
     *   ↓
     *              강남역 <--------10-------> 양재역
     *
     * To-be
     * 정자역 <--6--> 강남역 <--------10-------> 양재역
     */
    @DisplayName("지하철 노선에 새로운 머리 구간을 등록")
    @Test
    void leftExtendSection() {
        // Given
        Map<String, String> section = createSectionCreateParams(정자역, 강남역);

        // When
        지하철_노선에_지하철_구간_생성_요청(신분당선, section);

        // Then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(정자역, 강남역, 양재역);
    }

    /**
     * Given 노선에 존재하지 않는 역들로 구성된 구간을
     * When 지하철 노선에 새로운 구간 추가를 요청 하면
     * Then 등록되지 않는다
     *
     *       부산역 <---6---> 강원역
     *         ↓              ↓
     *         X              X
     * 강남역 <---6---> 정자역 <-4-> 양재역
     */
    @DisplayName("노선에 모두 존재하지 않는 역들로 이루어진 구간은 등록 불가")
    @Test
    void exceptionWhenRequestStationsNotExistOnLine() {
        // Given
        Long 부산역 = 지하철역_생성_요청("부산역").jsonPath().getLong("id");
        Long 강원역 = 지하철역_생성_요청("강원역").jsonPath().getLong("id");
        Map<String, String> section = createSectionCreateParams(부산역, 강원역);

        // When & Then
        ExtractableResponse<Response> responseOf구간생성 = 지하철_노선에_지하철_구간_생성_요청(신분당선, section);

        // Then
        assertThat(responseOf구간생성.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    /**
     * Given 노선에 새로운 구간을 추가하고
     * When 지하철 노선 조회를 요청 하면
     * Then 상행 종착역에서 시작하여 하행 종착역을 끝으로 하는 순서로 응답된다
     *
     * As-is
     * 강남역 <----6----> 정자역
     *                    ↓
     * 강남역 <---------10---------> 양재역
     *
     * To-be
     * 강남역 <----6----> 정자역 <-4-> 양재역
     *
     * 노선 조회 시, 강남역, 정자역, 양재역 순서 보장해야한다
     */
    @DisplayName("노선 조회시, 시작을 상행 종착역, 끝을 하행 종착역으로 하는 순서를 보장한다.")
    @Test
    void ensureSequentialOrderStations() {
        // Given
        Map<String, String> section = createSectionCreateParams(강남역, 정자역);

        // When
        지하철_노선에_지하철_구간_생성_요청(신분당선, section);

        // Then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 정자역, 양재역);
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 지하철 노선의 마지막 구간 제거를 요청 하면
     * Then 노선에 구간이 제거된다
     *
     * As-is
     * 강남역 <----6----> 정자역 <-4-> 양재역
     *
     * 제거요청: 양재역
     *
     * To-be
     * 강남역 <----6----> 정자역
     */
    @DisplayName("지하철 노선에 구간을 제거 - 맨 뒤")
    @Test
    void removeLineSection() {
        // given
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(정자역, 양재역));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 양재역);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역);
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 지하철 노선의 처음 구간 제거를 요청 하면
     * Then 노선에 구간이 제거된다
     *
     * As-is
     * 강남역 <----6----> 정자역 <-4-> 양재역
     *
     * 제거요청: 강남역
     *
     * To-be
     *                   정자역 <-4-> 양재역
     */
    @DisplayName("지하철 노선에 구간을 제거 - 맨 앞")
    @Test
    void removeHeadSection() {
        // given
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(정자역, 양재역));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 강남역);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(정자역, 양재역);
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 지하철 노선의 사이 구간 제거를 요청 하면
     * Then 노선에 구간이 제거되고, 제거한 역 기준의 양 옆 구간이 하나의 구간이 된다
     *
     * As-is
     * 강남역 <----6----> 정자역 <-4-> 양재역
     *
     * 제거요청: 정자역
     *
     * To-be
     * 강남역 <---------10---------> 양재역
     */
    @DisplayName("지하철 노선에 구간을 제거 - 사이")
    @Test
    void removeInternalSectionThenMergeSections() {
        // Given
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(정자역, 양재역));

        // When
        지하철_노선에_지하철_구간_제거_요청(신분당선, 정자역);

        // Then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역);
    }

    /**
     * Given 노선에 존재하지 않는 역을
     * When 제거 요청시
     * Then 실패한다
     *
     * 강남역 <---------10---------> 양재역
     *
     * 제거요청: 부산역
     */
    @DisplayName("지하철 노선의 구간에 존재하지 않는 역 제거 요청시, 제거 실패한다.")
    @Test
    void cannotRemoveLineSectionWhenNotExistStation() {
        // Given
        Long 부산역 = 지하철역_생성_요청("부산역").jsonPath().getLong("id");

        // When
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_제거_요청(신분당선, 부산역);

        // Then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    /**
     * Given 노선의 구간에 하나의 구간만 존재할때
     * When 제거 요청시
     * Then 실패한다
     *
     * 강남역 <---------10---------> 양재역
     *
     * 제거요청: 양재역
     */
    @DisplayName("지하철 노선의 구간에 하나의 구간만 존재할때 역 제거 요청하면, 제거 실패한다.")
    @Test
    void cannotRemoveSectionWhenExistOneSection() {
        // When
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_제거_요청(신분당선, 양재역);

        // Then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
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

    private Map<String, String> createSectionCreateParamsWithDistance(Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", distance + "");
        return params;
    }
}
