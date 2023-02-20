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
class SectionAcceptanceTest extends AcceptanceTest {
    private Long 신분당선_ID;

    private Long 강남역_ID;
    private Long 양재역_ID;

    private final int DISTANCE_OF_CREATE_LINE = 10;
    private final int SHORTER_DISTANCE = 6;
    private final int LONGER_DISTANCE = 12;

    /**
     * Given 지하철역과 노선 생성을 요청 하고
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        강남역_ID = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        양재역_ID = 지하철역_생성_요청("양재역").jsonPath().getLong("id");

        Map<String, String> lineCreateParams = createLineCreateParams(강남역_ID, 양재역_ID);
        신분당선_ID = 지하철_노선_생성_요청(lineCreateParams).jsonPath().getLong("id");
    }

    /**
     * When 지하철 노선 하행종점을 상행역으로 구간 추가를 요청 하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @DisplayName("지하철 노선 구간을 등록 - 하행 종점을 상행역으로 등록")
    @Test
    void addLineSection() {
        Long 정자역_ID = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        ExtractableResponse<Response> 지하철_노선에_지하철_구간_생성_응답 =
            지하철_노선에_지하철_구간_생성_요청(신분당선_ID,
                createSectionCreateParams(양재역_ID, 정자역_ID, SHORTER_DISTANCE));

        지하철_노선에_지하철_구간_생성됨(지하철_노선에_지하철_구간_생성_응답,
            new Long[]{강남역_ID, 양재역_ID, 정자역_ID});
    }

    /**
     * When 지하철 노선에 상행역을 새로운 역으로 구간 추가를 요청 하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @DisplayName("지하철 노선에 구간을 등록 - 상행종점이 새로운 역")
    @Test
    void addLineSectionWhenUpStationIsNew() {
        Long 정자역_ID = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        ExtractableResponse<Response> 지하철_노선에_지하철_구간_생성_응답 =
            지하철_노선에_지하철_구간_생성_요청(신분당선_ID,
                createSectionCreateParams(정자역_ID, 양재역_ID, SHORTER_DISTANCE));

        지하철_노선에_지하철_구간_생성됨(지하철_노선에_지하철_구간_생성_응답,
            new Long[]{강남역_ID, 정자역_ID, 양재역_ID});
    }

    /**
     * When 지하철 노선에 하행역을 새로운 역으로 구간 추가를 요청 하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @DisplayName("지하철 노선에 구간을 등록 - 하행종점이 새로운 역")
    @Test
    void addLineSectionWhenDownStationIsNew() {
        Long 정자역_ID = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        ExtractableResponse<Response> 지하철_노선에_지하철_구간_생성_응답 =
            지하철_노선에_지하철_구간_생성_요청(신분당선_ID,
                createSectionCreateParams(양재역_ID, 정자역_ID, SHORTER_DISTANCE));

        지하철_노선에_지하철_구간_생성됨(지하철_노선에_지하철_구간_생성_응답,
            new Long[]{강남역_ID, 양재역_ID, 정자역_ID});
    }

    /**
     * When 지하철 노선에 기존 구간보다 긴 구간을 추가 요청 하면
     * Then 예외가 발생한다.
     */
    @DisplayName("지하철 노선에 구간 등록 실패 - 기존 구간보다 긴 구간")
    @Test
    void addLineSectionWhenSectionIsLong() {
        Long 정자역_ID = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        ExtractableResponse<Response> 지하철_노선에_지하철_구간_생성_응답 =
            지하철_노선에_지하철_구간_생성_요청(신분당선_ID,
                createSectionCreateParams(강남역_ID, 정자역_ID, LONGER_DISTANCE));

        예외_발생함(지하철_노선에_지하철_구간_생성_응답, HttpStatus.BAD_REQUEST);
    }

    /**
     * When 지하철 노선에 상행역과 하행역이 모두 등록되어있는 구간을 추가요청하면
     * Then 예외가 발생한다.
     */
    @DisplayName("지하철 노선에 구간 등록 실패 - 상행역과 하행역 모두 이미 존재")
    @Test
    void addLineSectionWhenBothStationsExist() {
        ExtractableResponse<Response> 지하철_노선에_지하철_구간_생성_응답 =
            지하철_노선에_지하철_구간_생성_요청(신분당선_ID,
                createSectionCreateParams(강남역_ID, 양재역_ID, SHORTER_DISTANCE));

        예외_발생함(지하철_노선에_지하철_구간_생성_응답, HttpStatus.BAD_REQUEST);
    }

    /**
     * When 지하철 노선에 상행역과 하행역이 모두 등록되어 있지 않은 구간을 추가요청하면
     * Then 예외가 발생한다.
     */
    @DisplayName("지하철 노선에 구간 등록 실패 - 상행역과 하행역 모두 노선에 없음")
    @Test
    void addLineSectionWhenBothStationsDoNotExist() {
        Long 정자역_ID = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        Long 신사역_ID = 지하철역_생성_요청("신사역").jsonPath().getLong("id");
        ExtractableResponse<Response> 지하철_노선에_지하철_구간_생성_응답 =
            지하철_노선에_지하철_구간_생성_요청(신분당선_ID,
                createSectionCreateParams(정자역_ID, 신사역_ID, SHORTER_DISTANCE));

        예외_발생함(지하철_노선에_지하철_구간_생성_응답, HttpStatus.BAD_REQUEST);
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 지하철 노선의 마지막 구간 제거를 요청 하면
     * Then 노선에 구간이 제거된다
     */
    @DisplayName("지하철 노선에 구간을 제거")
    @Test
    void removeLineSection() {
        Long 정자역_ID = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선_ID,
            createSectionCreateParams(양재역_ID, 정자역_ID, SHORTER_DISTANCE));

        ExtractableResponse<Response> 지하철_노선에_지하철_구간_제거_응답 =
            지하철_노선에_지하철_구간_제거_요청(신분당선_ID, 정자역_ID);

        지하철_노선에_지하철_구간_제거됨(지하철_노선에_지하철_구간_제거_응답);
    }

    private void 지하철_노선에_지하철_구간_생성됨(ExtractableResponse<Response> response,
        Long[] orderedStationIds) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        ExtractableResponse<Response> 지하철_노선_조회_응답 = 지하철_노선_조회_요청(신분당선_ID);
        assertThat(지하철_노선_조회_응답.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(지하철_노선_조회_응답.jsonPath().getList("stations.id", Long.class))
            .containsExactly(orderedStationIds);
    }

    private void 지하철_노선에_지하철_구간_제거됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        ExtractableResponse<Response> 지하철_노선_조회_응답 = 지하철_노선_조회_요청(신분당선_ID);
        assertThat(지하철_노선_조회_응답.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(지하철_노선_조회_응답.jsonPath().getList("stations.id", Long.class))
            .containsExactly(강남역_ID, 양재역_ID);
    }

    private void 예외_발생함(ExtractableResponse<Response> response, HttpStatus status) {
        assertThat(response.statusCode())
            .isEqualTo(status.value());
    }

    private Map<String, String> createLineCreateParams(Long upStationId, Long downStationId) {
        Map<String, String> lineCreateParams;
        lineCreateParams = new HashMap<>();
        lineCreateParams.put("name", "신분당선");
        lineCreateParams.put("color", "bg-red-600");
        lineCreateParams.put("upStationId", Long.toString(upStationId));
        lineCreateParams.put("downStationId", Long.toString(downStationId));
        lineCreateParams.put("distance", Integer.toString(DISTANCE_OF_CREATE_LINE));
        return lineCreateParams;
    }

    private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId,
        int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", Long.toString(upStationId));
        params.put("downStationId", Long.toString(downStationId));
        params.put("distance", Integer.toString(distance));
        return params;
    }
}
