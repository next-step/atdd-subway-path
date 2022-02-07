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

@DisplayName("지하철 구간 관리 기능")
class LineSectionAcceptanceTest extends AcceptanceTest {
    private Long 신분당선;

    private Long 강남역;
    private Long 양재역;

    private final static int END_SECTION_DISTANCE = 1000;

    /**
     * Given 지하철역과 노선 생성을 요청 하고
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");

        Map<String, String> lineCreateParams = createLineCreateParams(강남역, 양재역, END_SECTION_DISTANCE);
        신분당선 = 지하철_노선_생성_요청(lineCreateParams).jsonPath().getLong("id");
    }

    /**
     * When 지하철 노선에 하행종점역 뒤에 새로운 구간 추가를 요청 하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @DisplayName("지하철 노선 하행종점역 뒤에 구간을 등록")
    @Test
    void addLineSectionToEndDown() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 100));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역, 정자역);
    }

    /**
     * When 지하철 노선에 상행종점역 앞에 새로운 구간 추가를 요청 하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @DisplayName("지하철 노선 상행종점역 앞에 구간을 등록")
    @Test
    void addLineSectionToEndUp() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(정자역, 강남역, 100));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(정자역, 강남역, 양재역);
    }

    /**
     * Given 지하철 노선에 새로운 구간을 등록하고
     * When 기존 구간 사이에 새로운 구간을 등록하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @DisplayName("지하철 노선에 구간을 등록 (기존 구간 사이에)")
    @Test
    void addLineSectionBetweenSections() {
        // when
        // 신분당선
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        Long 판교역 = 지하철역_생성_요청("판교역").jsonPath().getLong("id");
        // 강남 - 양재 - 정자
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 500));
        // 강남 - 양재 - 판교 - 정자
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 판교역, 400));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역, 판교역, 정자역);
    }

    /**
     * Given 지하철 노선에 새로운 구간을 등록하고
     * When 기존 구간 사이 역 사이 거리보다 더 큰 새로운 구간을 등록하면
     * Then 노선에 새로운 구간이 등록이 실패한다
     */
    @DisplayName("지하철 노선 기존 구간 사이에 신규 구간을 등록하면 실패한다 (신규 구간 역 사이 길이가 기존 구간사이 역 사이 길이보다 큼)")
    @Test
    void addLineSectionBetweenSectionsFailureGreaterThanOrEqualsDistance() {
        // when
        // 신분당선
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        Long 판교역 = 지하철역_생성_요청("판교역").jsonPath().getLong("id");
        // 강남 - 양재 - 정자
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 500));
        // 강남 - 양재 - 판교 - 정자
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 판교역, 600));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 지하철 노선에 새로운 구간을 등록하고
     * When 기존 구간 상행, 하행역에 모두 존재하는 새로운 구간을 등록하면
     * Then 노선에 새로운 구간이 등록이 실패한다
     */
    @DisplayName("지하철 노선 기존 구간 사이에 신규 구간을 등록하면 실패한다 (신규 구간의 상행, 하행역이 기존 구간의 상행, 하행역 모두 이미 등록되어있음)")
    @Test
    void addLineSectionBetweenSectionsFailureAlreadyRegisteredSection() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 양재역, 500));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 지하철 노선에 새로운 구간을 등록하고
     * Given 지하철 노선에 아직 등록되지 않은 상행, 하행 두 역을 등록하고
     * When 기존 구간 상행, 하행역에 어떠한 역도 존재하지 않는 새로운 구간을 등록하면
     * Then 노선에 새로운 구간이 등록이 실패한다
     */
    @DisplayName("지하철 노선 기존 구간 사이에 신규 구간을 등록하면 실패한다 (신규 구간의 상행, 하행역이 기존 구간의 상행, 하행역 어떠한 역도 등록되어있지 않음)")
    @Test
    void addLineSectionBetweenSectionsFailureNotExistStationsInSections() {
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        Long 판교역 = 지하철역_생성_요청("판교역").jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(정자역, 판교역, 500));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
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
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 500));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 정자역);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역);
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 지하철 노선의 중간 구간 제거를 요청 하면
     * Then 노선에 구간이 제거된다
     */
    @DisplayName("지하철 노선에 중간 구간을 제거")
    @Test
    void removeLineSectionByMiddle() {
        // given
        // 강남 - 양재 - 정자
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 500));

        // when
        // 강남 - 양재(X) - 정자
        지하철_노선에_지하철_구간_제거_요청(신분당선, 양재역);

        // then
        // 강남 - 정자
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 정자역);
    }

    /**
     * Given 지하철 노선에 오직 종점 구간만 등록하고
     * When 지하철 노선에 구간 제거를 요청 하면
     * Then 노선에 구간 제거 요청이 실패한다
     */
    @DisplayName("지하철 노선에 종점 구간만 있을 때 구간을 제거")
    @Test
    void removeLineSectionOnlyEndStationsFailure() {
        // given
        // 강남 - 양재

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_제거_요청(신분당선, 양재역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 지하철 노선에 새로운 구간을 등록하고
     * Given 지하철 노선에 등록하지 않을 새로운 지하철 역을 준비하고
     * When 등록하지 않은 새로운 지하철 역으로 지하철 노선의 구간 제거를 요청 하면
     * Then 노선에 구간 제거 요청이 실패한다
     */
    @DisplayName("지하철 노선에 종점 구간만 있을 때 구간을 제거")
    @Test
    void removeLineSectionHasNotAnySectionsByStationFailure() {
        // given
        // 강남 - 양재
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 500));
        Long 판교역 = 지하철역_생성_요청("판교역").jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_제거_요청(신분당선, 판교역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
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
