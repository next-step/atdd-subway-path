package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.handler.LineHandler.*;
import static nextstep.subway.handler.LineHandler.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.handler.StationHandler.지하철역_생성_요청;
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
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        LineResponse lineResponse = response.as(LineResponse.class);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(lineResponse.getStations()
                .stream()
                .map(StationResponse::getId))
                .containsExactly(강남역, 양재역, 정자역);
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

    /**
     * When 노선의 구간 사이에 새로운 구간을 추가하면
     * Then 새로운 구간이 추가된다.
     */
    @DisplayName("구간 사이에 새로운 구간 추가")
    @Test
    public void addSectionBetweenSavedSection() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, 새로운_구간_요청_값(강남역, 정자역, 4));

        // then
        ExtractableResponse<Response> 지하철_노선_조회_결과 = 지하철_노선_조회_요청(신분당선);
        LineResponse 신분당선 = 지하철_노선_조회_결과.as(LineResponse.class);

        assertThat(지하철_노선_조회_결과.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(신분당선.getStations()
                .stream()
                .map(StationResponse::getId))
                .containsExactly(강남역, 정자역, 양재역);
    }

    /**
     * When 노선의 구간 사이에 새로운 구간을 추가하고
     * When 새로운 구간을 한번 더 추가하면
     * Then 새로운 구간이 2개 추가된다.
     */
    @DisplayName("구간 사이에 2개 이상의 새로운 구간 추가")
    @Test
    public void addSectionBetweenSavedSectionLagerThan() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, 새로운_구간_요청_값(강남역, 정자역, 5));
        Long 구디역 = 지하철역_생성_요청("구디역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, 새로운_구간_요청_값(강남역, 구디역, 2));

        // then
        ExtractableResponse<Response> 지하철_노선_조회_결과 = 지하철_노선_조회_요청(신분당선);
        LineResponse 신분당선 = 지하철_노선_조회_결과.as(LineResponse.class);

        assertThat(지하철_노선_조회_결과.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(신분당선.getStations()
                .stream()
                .map(StationResponse::getId))
                .containsExactly(강남역, 양재역, 정자역, 구디역);
    }

    /**
     * When 노선의 상행 종점에 새로운 구간을 추가하면
     * Then 새로운 구간이 추가된다.
     */
    @DisplayName("상행 종점에 새로운 구간 추가")
    @Test
    public void addSectionAtStartingPoint() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, 새로운_구간_요청_값(정자역, 강남역, 4));

        // then
        ExtractableResponse<Response> 지하철_노선_조회_결과 = 지하철_노선_조회_요청(신분당선);
        LineResponse 신분당선 = 지하철_노선_조회_결과.as(LineResponse.class);

        assertThat(지하철_노선_조회_결과.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(신분당선.getStations()
                .stream()
                .map(StationResponse::getId))
                .containsExactly(정자역, 강남역, 양재역);
    }

    /**
     * When 노선의 하행 종점에 새로운 구간을 추가하면
     * Then 새로운 구간이 추가된다.
     */
    @DisplayName("하행 종점에 새로운 구간 추가")
    @Test
    public void addSectionAtEndPoint() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, 새로운_구간_요청_값(양재역, 정자역, 4));

        // then
        ExtractableResponse<Response> 지하철_노선_조회_결과 = 지하철_노선_조회_요청(신분당선);
        LineResponse 신분당선 = 지하철_노선_조회_결과.as(LineResponse.class);

        assertThat(지하철_노선_조회_결과.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(신분당선.getStations()
                .stream()
                .map(StationResponse::getId))
                .containsExactly(강남역, 양재역, 정자역);
    }

    /**
     * When 기존 구간의 길이보다 더 큰 길이의 구간을 추가하면
     * Then 구간을 추가할 수 없다.
     */
    @DisplayName("기존 구간 길이보다 더 큰 구간 길이를 등록하는 경우")
    @Test
    public void addSectionBetweenSavedSectionException() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        ExtractableResponse<Response> 지하철_노선에_지하철_구간_생성_결과 = 지하철_노선에_지하철_구간_생성_요청(신분당선,
                새로운_구간_요청_값(강남역, 정자역, 15));

        // then
        assertThat(지하철_노선에_지하철_구간_생성_결과.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 노선의 구간 사이에 새로운 구간을 추가하고
     * When 이미 등록된 구간에 대해서 추가할 경우
     * Then 구간을 추가할 수 없다.
     */
    @DisplayName("이미 등록된 구간을 추가하는 경우")
    @Test
    public void addSameSectionException() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, 새로운_구간_요청_값(강남역, 정자역, 4));

        // when
        ExtractableResponse<Response> 지하철_노선에_지하철_구간_생성_결과 = 지하철_노선에_지하철_구간_생성_요청(신분당선,
                새로운_구간_요청_값(강남역, 정자역, 4));

        // then
        assertThat(지하철_노선에_지하철_구간_생성_결과.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 노선의 구간 사이에 새로운 구간을 추가하고
     * When 등록된 구간에 새로운 구간의 상행역과 하행역이 존재하지 않는다면
     * Then 구간을 추가할 수 없다.
     */
    @DisplayName("새로운 구간의 상행역과 하행역이 존재하지 않는 경우")
    @Test
    public void addNotFoundSavedSectionException() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, 새로운_구간_요청_값(강남역, 정자역, 4));

        // when
        Long 구디역 = 지하철역_생성_요청("구디역").jsonPath().getLong("id");
        Long 봉천역 = 지하철역_생성_요청("봉천역").jsonPath().getLong("id");
        ExtractableResponse<Response> 지하철_노선에_지하철_구간_생성_결과 = 지하철_노선에_지하철_구간_생성_요청(신분당선,
                새로운_구간_요청_값(구디역, 봉천역, 4));

        // then
        assertThat(지하철_노선에_지하철_구간_생성_결과.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
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

    private Map<String, String> 새로운_구간_요청_값(Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", distance + "");
        return params;
    }
}