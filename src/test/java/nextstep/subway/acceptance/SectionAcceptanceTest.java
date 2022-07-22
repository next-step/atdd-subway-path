package nextstep.subway.acceptance;

import static nextstep.subway.acceptance.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선_조회_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_제거_요청;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;

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

        Map<String, String> lineCreateParams = createLineCreateParams(강남역, 양재역);
        신분당선 = 지하철_노선_생성_요청(lineCreateParams).jsonPath().getLong("id");
    }

    /**
     * When 지하철 노선 하행 종점에 새로운 구간 추가를 요청 하면
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
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역, 정자역)
        );
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
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역)
        );
    }

    /**
     * Given 새로운 역을 생성하고
     * When 지하철 노선 중간에 새로운 구간 추가를 요청 하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @DisplayName("지하철 구간 중간에 새로운 역이 정상적으로 추가되었습니다")
    @Test
    void addSectionInTheMiddleOfLine() {
        // given
        Long 중간역 = 지하철역_생성_요청("중간역").jsonPath().getLong("id");

        // when
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 중간역, 6));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 중간역, 양재역)
        );
    }

    /**
     * Given 새로운 역을 생성하고
     * When 지하철 노선 중간에 새로운 구간을 추가하면
     * Then 이미 등록된 구간의 경우 예외처리 된다
     */
    @DisplayName("기존 지하철 구간에 새로운 구간을 등록하는 경우, 이미 등록된 구간이면 예외처리 된다")
    @Test
    void exceptDuplicationExistingSection() {
        // given
        Long 중복된_강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        Long 중복된_양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> result = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(중복된_강남역, 중복된_양재역, 6));

        // then
        assertThat(result.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    /**
     * Given 새로운 역을 생성하고
     * When 지하철 노선 중간에 새로운 구간을 추가하면
     * Then 이미 등록된 구간의 길이보다 같거나 큰 경우 예외처리 된다
     */
    @ParameterizedTest(name = "등록한 구간의 길이가 {0}인경우, 기존 구간 길이보다 같거나 큼으로 예외처리 된다.")
    @ValueSource(ints = {10, 15})
    void exceptionDistanceOfNewSectionOverExistingSectionDistance(int distacne) {
        // given
        Long 중간역 = 지하철역_생성_요청("중간역").jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> result = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 중간역, distacne));

        // then
        assertThat(result.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    /**
     * Given 기존 구간에 등록되지 않은 새로운 역 2개를 생성하고
     * When 지하철 구간을 추가하면
     * Then 기존 구간과 연결할 역이 없으므로 예외처리가 된다
     */
    @DisplayName("신규 등록하는 구간의 역들이 기존 구간 중 하나의 역 이상 일치하지 않는 경우 예외처리 된다.")
    @Test
    void exceptionNotMatchedExistingSection() {
        // given
        Long 신논현역 = 지하철역_생성_요청("신논현역").jsonPath().getLong("id");
        Long 언재역 = 지하철역_생성_요청("언재역").jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> result = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(신논현역, 언재역, 3));

        // then
        assertThat(result.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
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
}
