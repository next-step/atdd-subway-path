package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.exception.ExceptionMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.acceptance.LineSteps.*;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
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

        Map<String, String> lineCreateParams = createLineCreateParams(강남역, 양재역, 10);
        신분당선 = 지하철_노선_생성_요청(lineCreateParams).jsonPath().getLong("id");
    }

    /**
     * When 지하철 노선 뒤쪽에 새로운 구간 추가를 요청 하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @DisplayName("지하철 노선에 구간을 등록 - 뒤에")
    @Test
    void addLineSectionNextToDown() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 6));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역, 정자역);
    }

    /**
     * When 지하철 노선 중간에 새로운 구간 추가를 요청 하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @DisplayName("지하철 노선에 구간을 등록 - 중간에(상행역이 같음)")
    @Test
    void addLineSectionBetweenUpAndDownInSameDownStation() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 정자역, 6));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 정자역, 양재역);
    }

    /**
     * When 지하철 노선 중간에 새로운 구간 추가를 요청 하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @DisplayName("지하철 노선에 구간을 등록 - 중간에(하행역이 같음)")
    @Test
    void addLineSectionBetweenUpAndDownInSameUpStation() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(정자역, 양재역, 6));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 정자역, 양재역);
    }

    /**
     * When 지하철 노선 앞에 새로운 구간 추가를 요청 하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @DisplayName("지하철 노선에 구간을 등록 - 앞에")
    @Test
    void addLineSectionFrontOfUp() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(정자역, 강남역, 6));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(정자역, 강남역, 양재역);
    }

    /**
     * When 중간 추가 시 기존과 거리가 같은 구간을 생성 요청하는 경우
     * Then BAD_REQUEST 를 반환한다.
     */
    @ParameterizedTest(name = "지하철 노선에 구간을 등록 실패 - 중간에(상행역 같음), 거리 같거나 큰 경우")
    @ValueSource(ints = {10, 11})
    void addLineSectionFailCauseSameDistanceInSameUpStation(int distance) {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 정자역, distance));
        String message = response.jsonPath().get("msg");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(message).isEqualTo(ExceptionMessage.TOO_LONG_DISTANCE_OF_SECTION.msg());
    }

    /**
     * When 중간 추가 시 기존과 거리가 같은 구간을 생성 요청하는 경우
     * Then BAD_REQUEST 를 반환한다.
     */
    @ParameterizedTest(name = "지하철 노선에 구간을 등록 실패 - 중간에(하행역 같음), 거리 같거나 큰 경우")
    @ValueSource(ints = {10, 11})
    void addLineSectionFailCauseSameDistanceInSameDownStation(int distance) {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(정자역, 양재역, distance));
        String message = response.jsonPath().get("msg");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(message).isEqualTo(ExceptionMessage.TOO_LONG_DISTANCE_OF_SECTION.msg());
    }

    /**
     * When 상,하행 모두 이미 존재하는 구간을 생성 요청하는 경우
     * Then BAD_REQUEST 를 반환한다.
     */
    @DisplayName("지하철 노선에 구간을 등록 실패 - 상,하행역 이미 존재")
    @Test
    void addLineSectionFailCauseSectionAlreadyExist() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 양재역, 6));
        String message = response.jsonPath().get("msg");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(message).isEqualTo(ExceptionMessage.ALREADY_EXIST_STATIONS.msg());
    }

    /**
     * When 상,하행 모두 노선에 존재하지 않는 구간을 생성 요청하는 경우
     * Then BAD_REQUEST 를 반환한다.
     */
    @DisplayName("지하철 노선에 구간을 등록 실패 - 상,하행역 존재하지 않음")
    @Test
    void addLineSectionFailCauseNoStationsInLine() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        Long 양재시민의숲 = 지하철역_생성_요청("양재시민의숲").jsonPath().getLong("id");
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재시민의숲, 정자역, 6));
        String message = response.jsonPath().get("msg");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(message).isEqualTo(ExceptionMessage.DOES_NOT_EXIST_STATIONS.msg());
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
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 6));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 정자역);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역);
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
