package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.acceptance.LineSteps.*;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;

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
     * When 지하철 노선에 새로운 역을 하행 종점으로 등록 요청 하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @DisplayName("지하철 노선에 구간을 등록(하행 종점)")
    @Test
    void addLineSection() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 6));

        // then
        지하철_노선에_구간등록됨(신분당선, 강남역, 양재역, 정자역);
    }

    /**
     * When 지하철 노선에 새로운 역을 상행 종점으로 등록 요청 하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @DisplayName("지하철 노선에 구간을 등록(상행 종점)")
    @Test
    void addLineSection2() {
        // when
        Long 신논현역 = 지하철역_생성_요청("신논현역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(신논현역, 강남역, 6));

        // then
        지하철_노선에_구간등록됨(신분당선, 신논현역, 강남역, 양재역);
    }

    /**
     * When 지하철 노선에 새로운 역을 역 사이에 새로운 역을 등록 요청 하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @DisplayName("지하철 노선에 구간을 등록(중간)")
    @Test
    void addLineSection3() {
        // when
        Long 중간역 = 지하철역_생성_요청("중간역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 중간역, 4));

        // then
        지하철_노선에_구간등록됨(신분당선, 강남역, 중간역, 양재역);
    }

    /**
     * When 지하철 노선에 새로운 역을 역 사이에 새로운 역을 등록 요청(기존 역 사이 길이보다 크거나 같은) 하면
     * Then 노선에 새로운 구간이 추가가 실패한다
     */
    @DisplayName("지하철 노선에 구간을 등록 실패(역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음)")
    @Test
    void addLineSectionFail() {
        // when
        Long 중간역 = 지하철역_생성_요청("중간역").jsonPath().getLong("id");
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 중간역, 10));

        // then
        지하철_노선에_구간등록_실패됨(response);
    }

    /**
     * When 지하철 노선에 새로운 역을 역 사이에 새로운 역을 등록 요청(이미 존재하는 구간) 하면
     * Then 노선에 새로운 구간이 추가가 실패한다
     */
    @DisplayName("지하철 노선에 구간을 등록 실패(상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음)")
    @Test
    void addLineSectionFail2() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 양재역, 6));

        // then
        지하철_노선에_구간등록_실패됨(response);
    }

    /**
     * When 지하철 노선에 새로운 역을 역 사이에 새로운 역을 등록 요청(구간에 없는 역) 하면
     * Then 노선에 새로운 구간이 추가가 실패한다
     */
    @DisplayName("지하철 노선에 구간을 등록 실패(상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음)")
    @Test
    void addLineSectionFail3() {
        // given
        Long 대림역 = 지하철역_생성_요청("대림역").jsonPath().getLong("id");
        Long 신풍역 = 지하철역_생성_요청("신풍역").jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(대림역, 신풍역, 6));

        // then
        지하철_노선에_구간등록_실패됨(response);
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 지하철 노선의 마지막 구간 제거(상행 종점역)를 요청 하면
     * Then 노선에 구간이 제거된다
     */
    @DisplayName("지하철 노선에 구간을 제거(상행 종점역)")
    @Test
    void removeLineSection() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 6));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 강남역);

        // then
        지하철_노선에_구간_제거됨(신분당선, 양재역, 정자역);
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 지하철 노선의 마지막 구간 제거(하행 종점역)를 요청 하면
     * Then 노선에 구간이 제거된다
     */
    @DisplayName("지하철 노선에 구간을 제거(하행 종점역)")
    @Test
    void removeLineSection2() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 6));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 정자역);

        // then
        지하철_노선에_구간_제거됨(신분당선, 강남역, 양재역);
    }

    /**
     * Given 지하철 노선에 구간이 1개일 때
     * When 지하철 노선의 구간 제거를 요청 하면
     * Then 노선에 구간 제거 실패한다.
     */
    @DisplayName("지하철 노선에 구간 1개일 때, 구간을 제거 - 실패")
    @Test
    void removeLineSectionFail() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_제거_요청(신분당선, 강남역);

        // then
        지하철_노선_구간_제거_실패됨(response);
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

    private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", distance + "");
        return params;
    }
}
