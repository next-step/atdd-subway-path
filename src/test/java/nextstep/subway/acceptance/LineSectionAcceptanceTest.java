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
import static org.junit.jupiter.api.Assertions.assertAll;

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
     * Given 지하철역과 노선 생성(A-C)을 요청 하고
     * When 해당 노선에 신규 구간(A-B)를 추가하는 경우
     * Then A역을 기준으로 구간이 추가된다(A-B, B-C)
     */
    @DisplayName("역 사이에 새로운 역을 등록")
    @Test
    void addSectionToMiddleOfLine() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 정자역));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 정자역, 양재역);
    }

    /**
     * Given 지하철역과 노선 생성(A-C)을 요청 하고
     * When 해당 노선에 신규 구간(B-A)를 추가하는 경우
     * Then 새로운 역(B)을 상행 종점으로 구간이 추가된다(B-A, A-C)
     */
    @DisplayName("새로운 역을 상행 종점으로 등록")
    @Test
    void addSectionToStartOfLine() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(정자역, 강남역));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(정자역, 강남역, 양재역);
    }

    /**
     * Given 지하철역과 노선 생성(A-C)을 요청 하고
     * When 해당 노선에 신규 구간(C-B)를 추가하는 경우
     * Then 새로운 역(B)을 하행 종점으로 구간이 추가된다(A-C, C-B)
     */
    @DisplayName("새로운 역을 하행 종점으로 등록")
    @Test
    void addSectionToEndOfLine() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역, 정자역);
    }

    /**
     * Given 지하철역과 노선 생성(A-C)을 요청 하고
     * When 해당 노선에 기존 길이보다 같거나 긴 신규 구간(A-B)를 추가하는 경우
     * Then 예외가 발생한다
     */
    @DisplayName("기존 구간 길이보다 같거나 긴 경우 역 사이에 새로운 역 등록 실패")
    @Test
    void failToAddSectionToMiddleOfLine() {
        // given
        Long 신규_추가역1 = 지하철역_생성_요청("신규_추가역1").jsonPath().getLong("id");

        // when(then)
        assertAll(() -> {
            ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 신규_추가역1, 10));
            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertThat(response.jsonPath().getString("errorMessage")).isEqualTo("구간 거리가 같거나 커 역 중간에 등록이 불가합니다.");

            response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 신규_추가역1, 13));
            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertThat(response.jsonPath().getString("errorMessage")).isEqualTo("구간 거리가 같거나 커 역 중간에 등록이 불가합니다.");
        });
    }

    /**
     * Given 지하철역과 노선 생성(A-C)을 요청 하고
     * When 해당 노선에 신규 구간(A-C)를 추가하는 경우
     * Then 예외가 발생한다
     */
    @DisplayName("상하행역 모두 존재하는 경우 등록 실패")
    @Test
    void failToAddSectionIfAllStationsExist() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 양재역));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.jsonPath().getString("errorMessage")).isEqualTo("신규 구간의 역이 이미 존재합니다.");
    }

    /**
     * Given 지하철역과 노선 생성(A-C)을 요청 하고
     * When 해당 노선에 신규 구간(B-D)를 추가하는 경우
     * Then 예외가 발생한다
     */
    @DisplayName("상하행역 모두 존재하지 않는 경우 등록 실패")
    @Test
    void failToAddSectionIfNoStationsExists() {
        // given
        Long 신규_추가역1 = 지하철역_생성_요청("신규_추가역1").jsonPath().getLong("id");
        Long 신규_추가역2 = 지하철역_생성_요청("신규_추가역2").jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(신규_추가역1, 신규_추가역2));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.jsonPath().getString("errorMessage")).isEqualTo("신규 구간의 역과 일치하는 역이 존재하지 않습니다.");
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

    private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", distance + "");
        return params;
    }

    private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId) {
        return createSectionCreateParams(upStationId, downStationId, 6);
    }
}
