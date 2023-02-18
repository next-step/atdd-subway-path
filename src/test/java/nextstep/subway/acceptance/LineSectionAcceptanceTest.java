package nextstep.subway.acceptance;

import static nextstep.subway.acceptance.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선_조회_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_제거_요청;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static nextstep.subway.exception.ErrorCode.DUPLICATED_SECTION;
import static nextstep.subway.exception.ErrorCode.INVALID_DISTANCE;
import static nextstep.subway.exception.ErrorCode.INVALID_SECTION;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

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
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 6));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역,
            정자역);
    }

    /**
     * When 지하철 노선 사이에 새로운 역 추가를 요청하면
     * Then 지하철 노선 사이에 새로운 역이 추가된다
     */
    @DisplayName("지하철 노선 역 사이에 새로운 역을 등록")
    @Test
    void addLineSectionInMiddle() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 정자역, 6));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 정자역,
            양재역);
    }

    /**
     * When 새로운 역을 노선의 상행 종점으로 추가를 요청하면
     * Then 지하철 노선의 상행 종점 역으로 추가된다
     */
    @DisplayName("새로운 역을 노선의 상행 종점으로 등록")
    @Test
    void addLineSectionToHead() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(정자역, 강남역, 6));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(정자역, 강남역,
            양재역);
    }

    /**
     * When 지하철 노선 사이에 기존 역 사이 길이보다 길거나 같은 새로운 구간 추가를 요청하면
     * Then INVALID_DISTANCE 예외가 발생한다.
     */
    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음.")
    @Test
    void addLineSectionInMiddleWithDistanceError() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선,
            createSectionCreateParams(강남역, 정자역, 10));

        // then
        assertThat(response.jsonPath().getString("message")).isEqualTo(
            INVALID_DISTANCE.getMessage());

    }

    /**
     * When 지하철 노선 사이에 이미 노선에 모두 등륵되어 있는 구간 추가를 요청하면
     * Then 예외가 발생한다.
     */
    @DisplayName("행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    @Test
    void addLineSectionInMiddleWithDuplicatedError() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선,
            createSectionCreateParams(강남역, 양재역, 6));

        // then
        assertThat(response.jsonPath().getString("message")).isEqualTo(
            DUPLICATED_SECTION.getMessage());

    }

    /**
     * When 지하철 노선 사이에 새로운 역 추가를 요청하면
     * Then 지하철 노선 사이에 새로운 역이 추가된다
     */
    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
    @Test
    void addLineSectionInMiddleWithNotExistError() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        Long 미금역 = 지하철역_생성_요청("미금역").jsonPath().getLong("id");
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선,
            createSectionCreateParams(정자역, 미금역, 6));

        // then
        assertThat(response.jsonPath().getString("message")).isEqualTo(
            INVALID_SECTION.getMessage());
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
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역,
            양재역);
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

    private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId,
        int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", distance + "");
        return params;
    }
}
