package nextstep.subway.line.acceptance;

import nextstep.subway.AcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.steps.LineSectionSteps.*;
import static nextstep.subway.steps.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관리 기능")
class SectionAcceptanceTest extends AcceptanceTest {
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
        신분당선 = 지하철_노선_생성_요청("신분당선", "red").jsonPath().getLong("id");
    }

    /**
     * Given 지하철 노선에 구간이 하나도 없을 때
     * When 지하철 노선에 새로운 구간 추가를 요청 하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @DisplayName("지하철 노선에 첫 구간을 추가")
    @Test
    void 구간_추가1() {
        // when
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 양재역, 6));

        // then
        노선에_역들이_순서대로_존재한다(신분당선, 강남역, 양재역);
    }

    /**
     * Given 지하철 노선에 구간이 존재할 때
     * When 새로운 역을 상행이나 하행 종점으로 등록할 경우
     * Then 노선에 새로운 구간이 추가된다
     */
    @DisplayName("새로운 역을 상행이나 하행 종점으로 등록할 경우 구간이 추가된다.")
    @Test
    void 구간_추가2() {
        // given
        Long 교대역 = 지하철역_생성_요청("교대역").jsonPath().getLong("id");
        Long 서초역 = 지하철역_생성_요청("서초역").jsonPath().getLong("id");

        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 양재역, 6));

        // when
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(교대역, 강남역, 6));
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 서초역, 6));

        // then
        노선에_역들이_순서대로_존재한다(신분당선, 교대역, 강남역, 양재역, 서초역);
    }

    /**
     * Given 지하철 노선에 구간이 존재할 때
     * When 구간 사이에 새로운 구간을 등록할 경우 중간에 추가할 구간의 길이가 기존 구간의 길이보다 짧다면
     * Then 노선에 새로운 구간이 추가된다
     */
    @DisplayName("역 사이에 새로운 역을 등록할 경우")
    @Test
    void 구간_추가3() {
        // given
        Long 교대역 = 지하철역_생성_요청("교대역").jsonPath().getLong("id");

        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 양재역, 6));

        // when
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 교대역, 2));

        // then
         노선에_역들이_순서대로_존재한다(신분당선, 강남역, 교대역, 양재역);
    }

    /**
     * Given 지하철 노선에 구간이 존재할 때
     * When 구간 사이에 새로운 구간을 등록할 경우 중간에 추가할 구간의 길이가 기존 구간의 길이보다 크거나 같다면
     * Then 추가할 수 없다
     */
    @DisplayName("기존 구간 사이에 추가할 구간의 길이가 기존 구간의 길이보다 크거나 같을 수 없다.")
    @Test
    void 구간_추가_예외1() {
        // given
        int distance = 6;
        Long 교대역 = 지하철역_생성_요청("교대역").jsonPath().getLong("id");

        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 양재역, distance));

        // when + then
        노선에_역을_추가할수_없다(강남역, 교대역, distance);
        노선에_역을_추가할수_없다(교대역, 양재역, distance);
    }

    /**
     * Given 지하철 노선에 구간이 존재하는 상황에서
     * When 추가할 구간의 상행역과 하행역이 이미 노선에 모두 등록되어 있다면
     * Then 추가할 수 없다
     */
    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    @Test
    void 구간_추가_예외2() {
        // given
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 양재역, 6));

        // when + then
        노선에_역을_추가할수_없다(강남역, 양재역, 5);
    }

    /**
     * Given 지하철 노선에 구간이 존재하는 상황에서
     * When 추가할 구간의 상행역과 하행역 모두 노선에 포함되어있지 않으면
     * Then 추가할 수 없다
     */
    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
    @Test
    void 구간_추가_예외3() {
        // given
        Long 교대역 = 지하철역_생성_요청("교대역").jsonPath().getLong("id");
        Long 서초역 = 지하철역_생성_요청("서초역").jsonPath().getLong("id");

        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 양재역, 6));

        // when + then
        노선에_역을_추가할수_없다(교대역, 서초역, 5);
    }

    /**
     * Given 지하철 노선에 구간이 둘 이상 있을 때
     * When 지하철 노선의 마지막 구간 제거를 요청 하면
     * Then 노선에 구간이 제거된다
     */
    @DisplayName("지하철 노선에서 마지막 구간을 제거")
    @Test
    void 구간_제거1() {
        // given
        Long 교대역 = 지하철역_생성_요청("교대역").jsonPath().getLong("id");

        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 양재역, 6));
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 교대역, 6));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 교대역);

        // then
        노선에_역들이_순서대로_존재한다(신분당선, 강남역, 양재역);
    }

    /**
     * Given 지하철 노선에 구간이 하나만 있을 때
     * When 지하철 노선의 마지막 구간 제거를 요청 하면
     * Then 제거에 실패한다
     */
    @DisplayName("지하철 노선에서 구간이 하나면 제거할 수 없다")
    @Test
    void 구간_제거_예외1() {
        // given
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 양재역, 6));

        // when + then
        노선에서_구간을_제거할수_없다(신분당선, 양재역);
    }

    /**
     * Given 지하철 노선이 주어졌을 때
     * When 지하철 노선에 등록되어있지 않은 역을 제거하려 하면
     * Then 제거에 실패한다
     */
    @DisplayName("지하철 노선에 등록되어있지 않은 역을 제거할 수 없다.")
    @Test
    void 구간_제거_예외2() {
        // given
        Long 교대역 = 지하철역_생성_요청("교대역").jsonPath().getLong("id");
        Long 서초역 = 지하철역_생성_요청("서초역").jsonPath().getLong("id");

        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 양재역, 6));
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 서초역, 6));

        // when + then
        노선에서_구간을_제거할수_없다(신분당선, 교대역);
    }

    private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", distance + "");
        return params;
    }

    private void 노선에_역들이_순서대로_존재한다(Long lineId, Long... stationIds) {
        var response = 지하철_노선_조회_요청(lineId);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class))
                .containsExactly(stationIds);
    }

    private void 노선에_역을_추가할수_없다(Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = createSectionCreateParams(upStationId, downStationId, distance);
        var response = 지하철_노선에_지하철_구간_생성_요청(신분당선, params);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private void 노선에서_구간을_제거할수_없다(Long lineId, Long stationId) {
       var response = 지하철_노선에_지하철_구간_제거_요청(lineId, stationId);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
