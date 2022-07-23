package nextstep.subway.line.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.line.acceptance.LineSectionSteps.*;
import static nextstep.subway.station.acceptance.StationSteps.지하철역_생성_요청;
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
     * When 지하철 노선에 새로운 구간 추가를 요청 하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @DisplayName("지하철 노선에 구간을 등록")
    @Test
    void addLineSection() {
        // when
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 양재역, 6));

        // then
        노선에_역들이_존재한다(신분당선, 강남역, 양재역);
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
        Long 교대역 = 지하철역_생성_요청("교대역").jsonPath().getLong("id");

        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 양재역, 6));
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 교대역, 6));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 교대역);

        // then
        노선에_역들이_존재한다(신분당선, 강남역, 양재역);
    }

    /**
     * Given 지하철 구간이 주어졌을때
     * When 중간에 추가할 구간의 길이가 기존 구간의 길이보다 크거나 같다면
     * Then 등록할 수 없다
     */
    @DisplayName("기존 구간 사이에 추가할 구간의 길이가 기존 구간의 길이보다 크거나 같을 수 없다.")
    @Test
    void 구간_추가_예외1() {
        // given
        int distance = 6;
        Long 교대역 = 지하철역_생성_요청("교대역").jsonPath().getLong("id");

        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 양재역, distance));

        // when
        노선에_역을_추가할수_없다(강남역, 교대역, distance);
        노선에_역을_추가할수_없다(교대역, 양재역, distance);
    }

    private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", distance + "");
        return params;
    }

    private void 노선에_역들이_존재한다(Long lineId, Long... stationIds) {
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(lineId);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class))
                .containsExactly(stationIds);
    }

    private void 노선에_역을_추가할수_없다(Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = createSectionCreateParams(upStationId, downStationId, distance);
        var response = 지하철_노선에_지하철_구간_생성_요청(신분당선, params);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
