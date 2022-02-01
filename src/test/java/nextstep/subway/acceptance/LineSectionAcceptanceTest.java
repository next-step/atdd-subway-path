package nextstep.subway.acceptance;

import static nextstep.subway.acceptance.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선_조회_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_제거_요청;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

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

        Map<String, String> lineCreateParams = createLineCreateParams(강남역, 양재역, 10);
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
        지하철_노선_응답_확인(신분당선, 강남역, 양재역, 정자역);
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 추가한 구간 사이에 새로운 구간을 등록하면
     * Then 새로운 구간이 추가된다.
     */
    @DisplayName("기존 구간 사이에 새로운 구간을 등록한다.")
    @Test
    void addLineSection2() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");

        //when
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 정자역, 6));

        // then
        지하철_노선_응답_확인(신분당선, 강남역, 정자역, 양재역);
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 추가한 구간 사이보다 길이가 큰 새로운 구간을 등록하면
     * Then 등록이 되지 않는다.
     */
    @DisplayName("구간을 등록시 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록할 수 없다.")
    @Test
    void addLineSection3() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> addSectionResponse = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 정자역, 1_000_000));

        // then
        구간_등록_실패(addSectionResponse.statusCode(), 신분당선, 강남역, 양재역);
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 상행역과 하행역이 이미 등록되어 있는 구간을 등록하면
     * Then 등록이 되지 않는다.
     */
    @DisplayName("구간을 등록시 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 등록할 수 없다.")
    @Test
    void addLineSection4() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 양재역, 6));

        // then
        구간_등록_실패(response.statusCode(), 신분당선, 강남역, 양재역);
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 상행역과 하행역이 모두 기존 노선에 등록되어 있지 않은 구간을 등록하면
     * Then 등록이 되지 않는다.
     */
    @DisplayName("구간을 등록시 상행역과 하행역 둘 중 하나도 포함되어 있지 않으면 등록할 수 없다.")
    @Test
    void addLineSection5() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        Long 광교역 = 지하철역_생성_요청("광교역").jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> addSectionResponse = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(정자역, 광교역, 6));

        // then
        구간_등록_실패(addSectionResponse.statusCode(), 신분당선, 강남역, 양재역);
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 지하철 노선의 하행 종점역을 제거 요청하면
     * Then 노선에 하행 종점역을 포함한 구간이 제거된다.
     */
    @DisplayName("지하철 노선에 하행 종점역을 제거")
    @Test
    void removeLineSection() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 6));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 정자역);

        // then
        지하철_노선_응답_확인(신분당선, 강남역, 양재역);
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 지하철 노선의 상행 종점역 제거를 요청 하면
     * Then 노선에 상행 종점역을 포함한 구간이 제거된다
     */
    @DisplayName("지하철 노선에 상행 종점역 제거")
    @Test
    void removeLineSection2() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 6));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 강남역);

        // then
        지하철_노선_응답_확인(신분당선, 양재역, 정자역);
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 지하철 노선의 종점역이 아닌 역을 제거 요청하면
     * Then 노선에 해당 역을 포함한 노선 2개가 삭제되고, 해당 역의 상행역과 하행역을 이은 노선이 생긴다.
     */
    @DisplayName("지하철 노선에 상행 종점역 제거")
    @Test
    void removeLineSection3() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 6));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 양재역);

        // then
        지하철_노선_응답_확인(신분당선, 강남역, 정자역);
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 지하철 노선의 종점역이 아닌 역을 제거 요청하면
     * Then 노선에 해당 역을 포함한 노선 2개가 삭제되고, 해당 역의 상행역과 하행역을 이은 노선이 생긴다.
     */
    @DisplayName("지하철 노선에 종점역이 아닌역 제거")
    @Test
    void removeLineSection4() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 6));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 양재역);

        // then
        지하철_노선_응답_확인(신분당선, 강남역, 정자역);
    }

    /**
     * Given 구간이 하나인 노선에
     * When 구간 제거 요청을 하면
     * Then 구간 제거가 실패한다.
     */
    @DisplayName("구간이 하나인 지하철 노선 제거")
    @Test
    void removeLineSection5() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_제거_요청(신분당선, 강남역);

        //then
        구간_제거_실패(response.statusCode(), 신분당선, 강남역, 양재역);
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 해당 노선에 포함되지 않은 역을 제거 요청하면
     * Then 구간 제거가 실패한다.
     */
    @DisplayName("지하철 노선에 포함되지 않은 역 제거")
    @Test
    void removeLineSection6() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_제거_요청(신분당선, 정자역);

        //then
        구간_제거_실패(response.statusCode(), 신분당선, 강남역, 양재역);
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 존재하지 않은 역의 제거 요청을 하면
     * Then 구간 제거가 실패한다.
     */
    @DisplayName("전체 역 중에서 존재하지 않은 역을 구간에서 제거")
    @Test
    void removeLineSection7() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_제거_요청(신분당선, 100000L);

        구간_제거_실패(response.statusCode(), 신분당선, 강남역, 양재역);
    }

    private void 구간_등록_실패(int actual, Long lineId, Long... stationIds) {
        assertThat(actual).isEqualTo(HttpStatus.BAD_REQUEST.value());
        지하철_노선_응답_확인(lineId, stationIds);
    }

    private void 구간_제거_실패(int actual, Long lineId, Long... stationIds) {
        assertThat(actual).isEqualTo(HttpStatus.BAD_REQUEST.value());
        지하철_노선_응답_확인(lineId, stationIds);
    }

    private void 지하철_노선_응답_확인(Long lineId, Long... expectedStationIds) {
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(lineId);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(expectedStationIds);
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
