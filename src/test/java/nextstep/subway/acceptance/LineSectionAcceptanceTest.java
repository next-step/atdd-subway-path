package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;

import java.util.Map;

import static nextstep.subway.acceptance.LineSteps.*;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관리 기능")
class LineSectionAcceptanceTest extends AcceptanceTest {
    private Long 신분당선;

    private Long 강남역;
    private Long 양재역;
    private Long 정자역;
    private Long 합정역;

    final private int 거리 = 50;

    /**
     * Given 지하철역과 노선 생성을 요청 하고
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");
        정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        합정역 = 지하철역_생성_요청("합정역").jsonPath().getLong("id");

        Map<String, String> lineCreateParams = createLineCreateParams(강남역, 양재역);
        신분당선 = 지하철_노선_생성_요청(lineCreateParams).jsonPath().getLong("id");
    }

    /**
     * When 지하철 노선에 새로운 구간 추가를 요청 하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @DisplayName("지하철 노선에 마지막 구간을 추가")
    @Test
    void addLastLineSection() {
        // when
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 거리));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역, 정자역);
    }

    /**
     * When 지하철 노선 중간에 새로운 구간 추가를 요청 하면
     * Then 노선 중간에 새로운 구간이 추가된다
     */
    @DisplayName("지하철 노선 중간에 구간을 추가")
    @Test
    void addMiddleLineSection() {
        // when
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 정자역, 거리));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 정자역, 양재역);
    }

    /**
     * When 지하철 노선 최앞단에 새로운 구간 추가를 요청 하면
     * Then 노선 최앞단에 새로운 구간이 추가된다
     */
    @DisplayName("지하철 노선 최앞단에 구간을 추가")
    @Test
    void addFirstLineSection() {
        // when
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(정자역, 강남역, 거리));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(정자역, 강남역, 양재역);
    }

    /**
     * When 지하철 노선 중간 구간 길이가 초과하는 구간을 추가하면
     * Then 구간 추가가 실패한다.
     */
    @DisplayName("지하철 노선 중간에 잘못된 거리의 구간을 추가")
    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 101})
    void addWrongDistanceSection(int distance) {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 정자역, distance));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * When 이미 노선에 등록되어 있는 상행역과 하행역을 등록할 경우
     * Then 구간 추가가 실패한다.
     */
    @DisplayName("이미 노선에 모두 등록되어 있는 역으로 구간 추가")
    @Test
    void addAllInclusiveStationsSection() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 양재역, 거리));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
    }

    /**
     * When 노선에 모두 등록되지 않은 지하철역으로 구간 추가를 요청하면
     * Then 구간 추가가 실패한다.
     */
    @DisplayName("노선에 모두 등록 되어있지 않은 역으로 구간 추가")
    @Test
    void addNotAllInclusiveStationsSection() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(정자역, 합정역, 거리));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * When 노선에 동일한 지하철역으로 구간 추가를 요청하면
     * Then 구간 추가가 실패한다.
     */
    @DisplayName("상행역과 하행역을 동일한 지하철역으로 설정한 구간 추가")
    @Test
    void addAllSameStationsSection() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 강남역, 거리));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
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
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 거리));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 정자역);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역);
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 지하철 노선의 첫번째 역 제거를 요청 하면
     * Then 노선의 역이 제거된다
     */
    @DisplayName("지하철 노선의 첫번째 역을 제거")
    @Test
    void removeFirstStation() {
        // given
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 거리));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 강남역);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(양재역, 정자역);
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 지하철 노선의 중간 역 제거를 요청 하면
     * Then 노선의 역이 제거된다
     */
    @DisplayName("지하철 노선의 중간 역을 제거")
    @Test
    void removeMiddleStation() {
        // given
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 거리));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 양재역);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 정자역);
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 지하철 노선의 마지막 역 제거를 요청 하면
     * Then 노선의 역이 제거된다
     */
    @DisplayName("지하철 노선의 마지막 역을 제거")
    @Test
    void removeLastStation() {
        // given
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 거리));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 정자역);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역);
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 지하철 노선에 존재하지 않는 구간 제거를 요청 하면
     * Then 노선의 구간 제거가 실패한다.
     */
    @DisplayName("지하철 노선의 존재하지 않는 구간을 제거")
    @Test
    void removeNotExistStation() {
        // given
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 거리));

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_제거_요청(신분당선, 합정역);

        // then
        지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * When 지하철 노선에 구간이 하나만 있을 때 구간 제거를 요청 하면
     * Then 노선의 구간 제거가 실패한다.
     */
    @DisplayName("지하철 노선 구간이 하나만 있을 때 첫번째 구간을 제거")
    @Test
    void removeFirstOnlyOneSection() {
        // when
        ExtractableResponse<Response> firstStationRemoveResponse = 지하철_노선에_지하철_구간_제거_요청(신분당선, 강남역);

        // then
        지하철_노선_조회_요청(신분당선);
        assertThat(firstStationRemoveResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * When 지하철 노선에 구간이 하나만 있을 때 구간 제거를 요청 하면
     * Then 노선의 구간 제거가 실패한다.
     */
    @DisplayName("지하철 노선 구간이 하나만 있을 때 마지막 구간을 제거")
    @Test
    void removeLastOnlyOneSection() {
        // when
        ExtractableResponse<Response> firstStationRemoveResponse = 지하철_노선에_지하철_구간_제거_요청(신분당선, 양재역);

        // then
        지하철_노선_조회_요청(신분당선);
        assertThat(firstStationRemoveResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private Map<String, String> createLineCreateParams(Long upStationId, Long downStationId) {
        Map<String, String> lineCreateParams = Map.of(
                "name", "신분당선",
                "color", "bg-red-600",
                "upStationId", upStationId.toString(),
                "downStationId", downStationId.toString(),
                "distance", String.valueOf(100)
        );

        return lineCreateParams;
    }

    private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = Map.of(
                "upStationId", upStationId.toString(),
                "downStationId", downStationId.toString(),
                "distance", String.valueOf(distance)
        );

        return params;
    }
}
