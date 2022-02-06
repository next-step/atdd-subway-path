package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.acceptance.LineSteps.*;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;

@DisplayName("지하철 구간 관리 기능")
class LineSectionAcceptanceTest extends AcceptanceTest {
    private static final int DEFAULT_DISTANCE = 10;
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
     * When 지하철 노선의 처음에 새로운 구간 추가를 요청 하면
     * Then 노선의 처음에 새로운 구간이 추가된다
     */
    @DisplayName("지하철 노선의 처음에 구간을 등록")
    @Test
    void addLineSectionFirst() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(정자역, 강남역));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        지하철_노선에_지하철_구간_생성_완료(response, 정자역, 강남역, 양재역);
    }

    /**
     * When 지하철 노선의 마지막에 새로운 구간 추가를 요청 하면
     * Then 노선의 마지막에 새로운 구간이 추가된다
     */
    @DisplayName("지하철 노선의 마지막에 구간을 등록")
    @Test
    void addLineSectionLast() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        지하철_노선에_지하철_구간_생성_완료(response, 강남역, 양재역, 정자역);
    }

    /**
     * Given 지하철 노선의 처음에 새로운 구간 추가를 요청하고
     * When 지하철 노선의 중간에 새로운 구간 추가를 요청 하면
     * Then 노선의 중간에 새로운 구간이 추가된다
     */
    @DisplayName("지하철 노선의 중간에 구간을 등록")
    @Test
    void addLineSectionMiddle() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(정자역, 강남역));

        // when
        Long 판교역 = 지하철역_생성_요청("판교역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 판교역, 6));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        지하철_노선에_지하철_구간_생성_완료(response, 정자역, 강남역, 판교역, 양재역);
    }

    /**
     * Given 지하철 노선의 처음에 새로운 구간 추가를 요청하고
     * When 지하철 노선의 중간에 기존에 등록된 구간보다 큰 거리를 갖는 새로운 구간 추가를 요청 하면
     * Then 구간 추가에 실패한다
     */
    @DisplayName("지하철 노선의 중간에 기존에 등록된 구간보다 큰 거리를 갖는 구간을 등록")
    @ParameterizedTest
    @ValueSource(ints = {DEFAULT_DISTANCE, DEFAULT_DISTANCE + 1})
    void addLineSectionWithBiggerDistance(int distance) {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(정자역, 강남역));

        // when
        Long 판교역 = 지하철역_생성_요청("판교역").jsonPath().getLong("id");
        ExtractableResponse<Response> response =
                지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 판교역, distance));

        // then
        지하철_노선에_지하철_구간_생성_실패(response);
    }

    /**
     * Given 지하철 노선의 처음에 새로운 구간 추가를 요청하고
     * When 지하철 노선의 중간에 기존에 등록된 상행역과 하행역을 갖는 새로운 구간 추가를 요청 하면
     * Then 구간 추가에 실패한다
     */
    @DisplayName("지하철 노선의 중간에 기존에 등록된 상행역과 하행역을 갖는 구간을 등록")
    @Test
    void addLineSectionWithDuplicateStation() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(정자역, 강남역));

        // when
        ExtractableResponse<Response> response =
                지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 정자역));

        // then
        지하철_노선에_지하철_구간_생성_실패(response);
    }

    /**
     * Given 지하철 노선의 처음에 새로운 구간 추가를 요청하고
     * When 지하철 노선의 중간에 기존에 등록되지 않은 상행역과 하행역을 갖는 새로운 구간 추가를 요청 하면
     * Then 구간 추가에 실패한다
     */
    @DisplayName("지하철 노선의 중간에 기존에 등록되지 않은 상행역과 하행역을 갖는 구간을 등록")
    @Test
    void addLineSectionWithNotContainsStation() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(정자역, 강남역));

        Long 신림역 = 지하철역_생성_요청("신림역").jsonPath().getLong("id");
        Long 봉천역 = 지하철역_생성_요청("봉천역").jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response =
                지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(신림역, 봉천역));

        // then
        지하철_노선에_지하철_구간_생성_실패(response);
    }

    /**
     * Given 지하철 노선의 처음에 새로운 구간 추가를 요청 하고
     * When 지하철 노선의 첫 번째 구간 제거를 요청 하면
     * Then 노선의 구간이 제거된다
     */
    @DisplayName("지하철 노선의 첫 번째 구간을 제거")
    @Test
    void removeLineSectionFrist() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(정자역, 강남역));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 정자역);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        지하철_노선에_지하철_구간_제거_완료(response, 강남역, 양재역);
    }

    /**
     * Given 지하철 노선의 마지막에 새로운 구간 추가를 요청 하고
     * When 지하철 노선의 마지막 구간 제거를 요청 하면
     * Then 노선의 구간이 제거된다
     */
    @DisplayName("지하철 노선의 마지막 구간을 제거")
    @Test
    void removeLineSectionLast() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 정자역);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        지하철_노선에_지하철_구간_제거_완료(response, 강남역, 양재역);
    }

    /**
     * Given 지하철 노선의 마지막에 새로운 구간 추가를 요청 하고
     * When 지하철 노선의 중간 구간 제거를 요청 하면
     * Then 노선의 구간이 제거된다
     */
    @DisplayName("지하철 노선의 중간 구간을 제거")
    @Test
    void removeLineSectionMiddle() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 양재역);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        지하철_노선에_지하철_구간_제거_완료(response, 강남역, 정자역);
    }

    /**
     * When 지하철 노선의 히니쁜인 구간 제거를 요청 하면
     * Then 노선의 구간 제거에 실패한다
     */
    @DisplayName("지하철 노선의 하나뿐인 구간을 제거")
    @Test
    void removeLineSectionLastOne() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_제거_요청(신분당선, 양재역);

        // then
        지하철_노선에_지하철_구간_제거_실패(response);
    }

    /**
     * When 지하철 노선에 기존에 등록되지 않은 구간 제거를 요청 하면
     * Then 노선의 구간 제거에 실패한다
     */
    @DisplayName("지하철 노선에 기존에 등록되지 않은 구간을 제거")
    @Test
    void removeLineSectionWithNotContains() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_제거_요청(신분당선, 정자역);

        // then
        지하철_노선에_지하철_구간_제거_실패(response);
    }

    private Map<String, String> createLineCreateParams(Long upStationId, Long downStationId) {
        Map<String, String> lineCreateParams;
        lineCreateParams = new HashMap<>();
        lineCreateParams.put("name", "신분당선");
        lineCreateParams.put("color", "bg-red-600");
        lineCreateParams.put("upStationId", upStationId + "");
        lineCreateParams.put("downStationId", downStationId + "");
        lineCreateParams.put("distance", DEFAULT_DISTANCE + "");
        return lineCreateParams;
    }

    private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", DEFAULT_DISTANCE + "");
        return params;
    }

    private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", distance + "");
        return params;
    }
}
