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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
        var 광교역 = 지하철역_생성_요청("광교역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 광교역));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역, 광교역);
    }



    /**
     * When 기존 구간 상행선 기존으로 중간에 신규 구간을 추가한다.
     * Then 생성된 구간을 확인할 수 있다.
     */
    @DisplayName("기존 구간 사이에 새로운 구간 추가")
    @Test
    void addMiddleLineSection() {
        //when
        var 중간역 = 지하철역_생성_요청("중간역").jsonPath().getLong("id");
        Map<String, String> addMiddleParam = createSectionCreateParams(강남역, 중간역);
        지하철_노선에_지하철_구간_생성_요청(신분당선, addMiddleParam);

        //then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 중간역, 양재역);

    }

    /**
     * when 기존 구간 상행성 기준으로 길이가 큰 신규 구간을 추가한다.
     * then error 확인
     */
    @DisplayName("예외케이스 1. 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록 할 수 없다.")
    @Test
    void exceptionCase1() {
        //when
        var 중간역 = 지하철역_생성_요청("중간역").jsonPath().getLong("id");
        Map<String, String> addMiddleParam = createSectionCreateParams(강남역, 중간역, 10);
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, addMiddleParam);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    /**
     * when 기존 구간에 상행선과 하행선 둘 중 하나도 포함되어 있지 않은 구간을 추가한다.
     * then error 확인
     */
    @DisplayName("예외케이스 2. 상행선과 하행역이 이미 노선에 등록되어있다면 추가할 수 없다.")
    @Test
    void exceptionCase2() {
        //when
        Map<String, String> addMiddleParam = createSectionCreateParams(강남역, 양재역, 3);
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, addMiddleParam);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    /**
     * when 기존 구간에 상행선과 하행선 둘 중 하나도 포함되어 있지 않은 구간을 추가한다.
     * then error 확인
     */
    @DisplayName("예외케이스 3. 상행선과 하행선 둘 중 하나도 포함되어 있지 않은 구간을 추가할 수 없다.")
    @Test
    void exceptionCase3() {
        //when
        var 새로운역 = 지하철역_생성_요청("새로운역").jsonPath().getLong("id");
        Long 새로운역1 = 지하철역_생성_요청("새로운역1").jsonPath().getLong("id");
        Map<String, String> addMiddleParam = createSectionCreateParams(새로운역, 새로운역1, 3);
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, addMiddleParam);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
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
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 정자역);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역);
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

    private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", distance + "");
        return params;
    }
}
