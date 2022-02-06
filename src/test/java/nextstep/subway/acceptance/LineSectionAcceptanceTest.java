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
    private Long 신도림역;
    private Long 문래역;

    /**
     * Given 지하철역과 노선 생성을 요청 하고
     */
    @BeforeEach
    public void setUp() {
        super.setUp();
        강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");
        신도림역 = 지하철역_생성_요청("신도림역").jsonPath().getLong("id");
        문래역 = 지하철역_생성_요청("문래역").jsonPath().getLong("id");

        Map<String, String> lineCreateParams = createLineCreateParams(강남역, 양재역, 10);
        신분당선 = 지하철_노선_생성_요청(lineCreateParams).jsonPath().getLong("id");
    }

    /**
     * When 지하철 노선에 A->C 구간에 A->B 구간 추가를 요청 하면
     * Then A->B, B->C 새로운 구간이 추가된다
     */
    @DisplayName("기존 구간의 왼쪽과 중간에 구간을 등록")
    @Test
    void addLeftAndMiddleSection() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 정자역, 5));
        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("sections.downStation.name", String.class)).containsExactly("강남역", "정자역");
        assertThat(response.jsonPath().getList("sections.upStation.name", String.class)).containsExactly("정자역", "양재역");
    }

    /**
     * When 지하철 노선에 A->C 구간에 B->C 구간 추가를 요청 하면
     * Then A->B, B->C 새로운 구간이 추가된다
     */
    @DisplayName("기존 구간의 중간과 오른쪽에 구간을 등록")
    @Test
    void addMiddleAndRightSection() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(정자역, 양재역, 5));
        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("sections.downStation.name", String.class)).containsExactly("강남역", "정자역");
        assertThat(response.jsonPath().getList("sections.upStation.name", String.class)).containsExactly("정자역", "양재역");
    }

    /**
     * When 지하철 노선에 A->C 구간에 C->B 구간 추가를 요청 하면
     * Then A->C, C->B 새로운 구간이 추가된다
     */
    @DisplayName("기존 구간의 오른쪽에 구간을 등록")
    @Test
    void addRightSection() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 5));
        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("sections.downStation.name", String.class)).containsExactly("강남역", "양재역");
        assertThat(response.jsonPath().getList("sections.upStation.name", String.class)).containsExactly("양재역", "정자역");
    }

    /**
     * When 지하철 노선에 A->C 구간에 B->A 구간 추가를 요청 하면
     * Then B->A, A->C 새로운 구간이 추가된다
     */
    @DisplayName("기존 구간의 왼쪽에 구간을 등록")
    @Test
    void addLeftSection() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(정자역, 강남역, 5));
        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("sections.downStation.name", String.class)).containsExactly("정자역", "강남역");
        assertThat(response.jsonPath().getList("sections.upStation.name", String.class)).containsExactly("강남역", "양재역");
    }
    /**
     * When 지하철 노선에 A->B 구간에 C->D 구간 추가를 요청 하면
     * Then 구간 생성이 실패한다.
     */
    @DisplayName("노선에 존재하지 않는 상행역과 하행역을 가지는 구간 생성 요청")
    @Test
    void NoMatchSectionException() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(신도림역,문래역, 5));
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * When 지하철 노선에 A->B 구간에 C->D 구간 추가를 요청 하면
     * Then 구간 생성이 실패한다.
     */
    @DisplayName("노선에 이미 존재하는 상행역과 하행역을 가지는 구간 생성 요청")
    @Test
    void AllMatchSectionException() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역,양재역, 5));
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
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 3));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 정자역);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역);
    }

    private Map<String, String> createLineCreateParams(Long downStationId, Long upStationId, int distance) {
        Map<String, String> lineCreateParams;
        lineCreateParams = new HashMap<>();
        lineCreateParams.put("name", "신분당선");
        lineCreateParams.put("color", "bg-red-600");
        lineCreateParams.put("downStationId", downStationId + "");
        lineCreateParams.put("upStationId", upStationId + "");
        lineCreateParams.put("distance", distance + "");
        return lineCreateParams;
    }

    private Map<String, String> createSectionCreateParams(Long downStationId, Long upStationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("downStationId", downStationId + "");
        params.put("upStationId", upStationId + "");
        params.put("distance", distance + "");
        return params;
    }
}
