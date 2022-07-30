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
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역, 정자역);
    }

    /**
     * when : 노선의 기존 구간 사이에 구간을 등록을 요청한다.
     * then : 노선의 새로운 구간이 등록된다.
     */
    @DisplayName("지하철 노선의 기존 구간 사이에 새로운 구간을 등록")
    @Test
    void addLineSectionBetweenSections() {

        // when
        final Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(정자역, 양재역));
        
        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 정자역, 양재역);
    }

    /**
     * when : 노선의 기존 구간 사이에 기존 구간의 거리보다 거리가 큰 구간을 등록 요청한다.
     * then : 예외를 발생시킨다.
     */
    @DisplayName("지하철 노선의 기존 구간 사이에 새롭게 등록될 구간의 거리가 더 클 경우")
    @Test
    void IllegalStateExceptionIfAddLineSectionGreatorThanExistSectionDistance() {

        // when
        final Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        ExtractableResponse<Response> 지하철_노선에_지하철_구간_생성_응답 = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(정자역, 양재역, "11"));

        // then
        assertThat(지하철_노선에_지하철_구간_생성_응답.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    /**
     * when : 노선의 기존 구간 사이에 기존 구간의 거리보다 거리가 동일한 구간을 등록 요청한다.
     * then : 예외를 발생시킨다.
     */
    @DisplayName("지하철 노선의 기존 구간 사이에 새롭게 등록될 구간의 거리가 동일할 경우")
    @Test
    void IllegalStateExceptionIfAddLineSectionEqualsExistSectionDistance() {

        // when
        final Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        ExtractableResponse<Response> 지하철_노선에_지하철_구간_생성_응답 = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(정자역, 양재역, "10"));

        // then
        assertThat(지하철_노선에_지하철_구간_생성_응답.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    /**
     * when : 노선의 기존 구간 사이에 거리가 0 혹은 음수인 구간을 등록을 요청한다.
     * then : 예외를 발생시킨다.
     */
    @DisplayName("지하철 노선의 기존 구간 사이에 새롭게 등록될 구간의 거리가 0 혹은 음수일 경우")
    @Test
    void IllegalArgumentExceptionIfAddLineSectionDistanceZeroOrNegative() {

        // when
        final Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        ExtractableResponse<Response> 지하철_노선에_지하철_구간_생성_응답 = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(정자역, 양재역, "0"));

        // then
        assertThat(지하철_노선에_지하철_구간_생성_응답.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    /**
     * when : 노선의 기존 구간 사이에 기존 구간과 동일한 구간을 등록을 요청한다.
     * then : 예외를 발생시킨다.
     */
    @DisplayName("지하철 노선에 새롭게 등록하려는 구간이 존재할 경우")
    void IllegalArgumentExceptionIfSectionExist() {

        // when
        ExtractableResponse<Response> 지하철_노선에_지하철_구간_생성_응답 = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 양재역, "5"));

        // then
        assertThat(지하철_노선에_지하철_구간_생성_응답.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    /**
     * when : 지하철 노선의 가장 Down 구간 하위에 구간을 등록 요청한다.
     * then : 구간이 등록된다.
     */
    @DisplayName("지하철 노선의 가장 Down 구간 하위에 구간 등록")
    @Test
    void addLineSectionToDownSection() {

        // when
        final Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        ExtractableResponse<Response> 지하철_노선에_지하철_구간_생성_응답 = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, "7"));

        // then
        ExtractableResponse<Response> 지하철_노선_응답 = 지하철_노선_조회_요청(신분당선);
        assertThat(지하철_노선_응답.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(지하철_노선_응답.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역, 정자역);
    }

    /**
     * when : 지하철 노선의 가장 Top 구간 위에 구간을 등록 요청한다.
     * then : 구간이 등록된다.
     */
    @DisplayName("지하철 노선의 가장 Top 구간 상위에 구간 등록")
    @Test
    void addLineSectionToTopSection() {

        // when
        final Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        ExtractableResponse<Response> 지하철_노선에_지하철_구간_생성_응답 = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(정자역, 강남역, "7"));

        // then
        ExtractableResponse<Response> 지하철_노선_응답 = 지하철_노선_조회_요청(신분당선);
        assertThat(지하철_노선_응답.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(지하철_노선_응답.jsonPath().getList("stations.id", Long.class)).containsExactly(정자역, 강남역, 양재역);
    }

    /**
     * when : 지하철 노선에 구간을 등록 요청한다.
     * then : 예외가 발생한다.
     */
    @DisplayName("지하철 노선 구간에 일치하지 않은 상행, 하행을 가진 구간을 등록")
    @Test
    void addLineSectionNotHasStations() {

        // when
        ExtractableResponse<Response> 지하철_노선에_지하철_구간_생성_응답 = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 양재역, "5"));

        // then
        assertThat(지하철_노선에_지하철_구간_생성_응답.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
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
        return createLineCreateParams(upStationId, downStationId, "10");
    }

    private Map<String, String> createLineCreateParams(Long upStationId, Long downStationId, String distance) {
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
        return createSectionCreateParams(upStationId, downStationId, "6");
    }

    private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId, String distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", distance);
        return params;
    }
}
