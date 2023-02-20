package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.domain.exception.SectionExceptionMessages;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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

    private Integer sectionDistance1 = 10;
    private Integer sectionDistance2 = 6;

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
     * When 새로운 역을 하행 종점으로 등록할 경우
     * Then 새로운 두 개의 구간이 생성된다.
     */
    @DisplayName("지하철 구간 등록 - 정상1 : 새로운 역이 하행 종점인 구간 추가")
    @Test
    void addLineSection_ValidCase1() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, sectionDistance2));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역, 정자역);
        assertThat(response.jsonPath().getInt("length")).isEqualTo(sectionDistance1 + sectionDistance2);
    }

    /**
     * When 새로운 역을 상행 종점으로 등록할 경우
     * Then 새로운 두 개의 구간이 생성된다.
     */
    @DisplayName("지하철 구간 등록 - 정상2 : 새로운 역이 상행 종점인 구간 추가")
    @Test
    void addLineSection_ValidCase2() {
        // when
        Long 신논현역 = 지하철역_생성_요청("신논현역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(신논현역, 강남역, sectionDistance2));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(신논현역, 강남역, 양재역);
        assertThat(response.jsonPath().getInt("length")).isEqualTo(sectionDistance1 + sectionDistance2);
    }

    /**
     * When 구간 사이에 새로운 구간을 추가하면
     * Then 새로운 두 개의 구간이 생성된다.
     */
    @DisplayName("지하철 구간 등록 - 정상3 : 기존 구간 사이에 새로운 구간 추가")
    @Test
    void addLineSection_ValidCase3() {
        // when
        Long 중간역 = 지하철역_생성_요청("중간역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 중간역, sectionDistance2));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 중간역, 양재역);
        assertThat(response.jsonPath().getInt("length")).isEqualTo(sectionDistance1);
    }

    /**
     * When 구간 사이에 새로운 구간을 추가하면
     *    - 단, 새로운 구간의 길이는 기존 구간의 길이보다 크거나 같다.
     * Then 구간이 추가되지 않는다.
     */
    @DisplayName("지하철 구간 등록 - 예외1 : 기존 구간 사이에 새로운 구간을 추가하려는데, 새로운 구간의 길이가 기존 구간의 길이 이상인 경우")
    @Test
    void addLineSection_InvalidCase1() {
        // when
        Long 중간역 = 지하철역_생성_요청("중간역").jsonPath().getLong("id");
        var 구간_생성_응답 = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 중간역, 50));

        // then
        assertThat(구간_생성_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(구간_생성_응답.asString()).isEqualTo(SectionExceptionMessages.INVALID_DISTANCE);

        var 노선_조회_응답 = 지하철_노선_조회_요청(신분당선);
        assertThat(노선_조회_응답.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역);
    }

    /**
     * When 상,하행역 모두 이미 노선에 존재하는 새로운 구간을 추가하려는 경우
     * Then 구간이 추가되지 않는다.
     */
    @DisplayName("지하철 구간 등록 - 예외2: 새로운 구간의 상,하행역 모두 이미 노선에 등록된 경우")
    @Test
    void addLineSection_InvalidCase2() {
        // when
        var 구간_생성_응답 = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 강남역, 10));

        // then
        assertThat(구간_생성_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(구간_생성_응답.asString()).isEqualTo(SectionExceptionMessages.ALREADY_EXIST);

        var 노선_조회_응답 = 지하철_노선_조회_요청(신분당선);
        assertThat(노선_조회_응답.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역);
    }

    /**
     * When 상,하행역 모두 노선에 존재하지 않는 새로운 구간을 추가하려는 경우
     * Then 구간이 추가되지 않는다.
     */
    @DisplayName("지하철 구간 등록 - 예외3: 새로운 구간의 상,하행역 모두 노선에 포함되지 않은 경우")
    @Test
    void addLineSection_InvalidCase3() {
        // given
        Long 신림역 = 지하철역_생성_요청("신림역").jsonPath().getLong("id");
        Long 서울대입구역 = 지하철역_생성_요청("서울대입구역").jsonPath().getLong("id");

        // when
        var 구간_생성_응답 = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(신림역, 서울대입구역, 10));

        // then
        assertThat(구간_생성_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(구간_생성_응답.asString()).isEqualTo(SectionExceptionMessages.NOTHING_EXIST);

        var 노선_조회_응답 = 지하철_노선_조회_요청(신분당선);
        assertThat(노선_조회_응답.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역);
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 상행 종점역 제거를 요청 하면
     * Then 해당 구간이 제거된다.
     */
    @DisplayName("지하철 구간 제거 - 정상1 : 상행 종점역 제거")
    @Test
    void removeLineSection_ValidCase1() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, sectionDistance2));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 강남역);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(양재역, 정자역);
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 하행 종점역 제거를 요청 하면
     * Then 해당 구간이 제거된다.
     */
    @DisplayName("지하철 구간 제거 - 정상2 : 하행 종점역 제거")
    @Test
    void removeLineSection_ValidCase2() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, sectionDistance2));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 정자역);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역);
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 구간 사이에 있는 역 제거를 요청 하면
     * Then 해당 구간이 제거된다.
     */
    @DisplayName("지하철 구간 제거 - 정상3 : 구간 사이에 위치한 역 제거")
    @Test
    void removeLineSection_ValidCase3() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, sectionDistance2));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 양재역);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 정자역);
    }

    /**
     * Given 구간이 하나인 지하철 노선에
     * When 구간 제거를 요청하면
     * Then 해당 구간이 제거되지 않는다.
     */
    @DisplayName("지하철 구간 제거 - 예외1 : 구간이 하나 노선 구간 제거")
    @Test
    void removeLineSection_InvalidCase1() {
        // when
        var 구간_제거_응답 = 지하철_노선에_지하철_구간_제거_요청(신분당선, 양재역);

        // then
        assertThat(구간_제거_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(구간_제거_응답.asString()).isEqualTo(SectionExceptionMessages.CANNOT_REMOVE_SECTION_WHEN_LINE_HAS_ONLY_ONE);

        var 노선_조회_응답 = 지하철_노선_조회_요청(신분당선);
        assertThat(노선_조회_응답.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역);
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 해당 노선에 포함되지 않은 역 제거를 요청하면
     * Then 구간이 제거되지 않는다.
     */
    @DisplayName("지하철 구간 제거 - 예외2 : 해당 노선에 포함되지 않은 역 제거")
    @Test
    void removeLineSection_InvalidCase2() {
    }

    private Map<String, String> createLineCreateParams(Long upStationId, Long downStationId) {
        Map<String, String> lineCreateParams;
        lineCreateParams = new HashMap<>();
        lineCreateParams.put("name", "신분당선");
        lineCreateParams.put("color", "bg-red-600");
        lineCreateParams.put("upStationId", upStationId + "");
        lineCreateParams.put("downStationId", downStationId + "");
        lineCreateParams.put("distance", sectionDistance1 + "");
        return lineCreateParams;
    }

    private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId, Integer distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", distance + "");
        return params;
    }
}
