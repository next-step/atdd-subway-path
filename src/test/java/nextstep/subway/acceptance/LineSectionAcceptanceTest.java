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

    private Long 이호선;

    private Long 강남역;
    private Long 역삼역;
    private Long 선릉역;

    /**
     * Given 지하철역과 노선 생성을 요청 하고
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        역삼역 = 지하철역_생성_요청("역삼역").jsonPath().getLong("id");
        선릉역 = 지하철역_생성_요청("선릉역").jsonPath().getLong("id");

        Map<String, String> lineCreateParams = createLineCreateParams(강남역, 역삼역);
        이호선 = 지하철_노선_생성_요청(lineCreateParams).jsonPath().getLong("id");
    }

    /**
     * When 지하철 노선에 역과 역 사이에 대한 구간 추가를 요청 하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @DisplayName("지하철 노선에 역과 역 사이에 대한 구간을 등록")
    @Test
    void addLineSectionBetweenStations() {
        // when
        지하철_노선에_지하철_구간_생성_요청(이호선, createSectionCreateParams(강남역, 선릉역));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(이호선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 선릉역, 역삼역);
    }

    /**
     * When 지하철 노선에 상행 종점역에 대한 새로운 구간 추가를 요청 하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @DisplayName("지하철 노선에 상행 종점역에 대한 구간을 등록")
    @Test
    void addLineSectionWithLastUpStation() {
        // when
        지하철_노선에_지하철_구간_생성_요청(이호선, createSectionCreateParams(선릉역, 강남역));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(이호선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(선릉역, 강남역, 역삼역);
    }

    /**
     * When 지하철 노선에 하행 종점역에 대한 새로운 구간 추가를 요청 하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @DisplayName("지하철 노선에 하행 종점역에 대한 구간을 등록")
    @Test
    void addLineSectionWithLastDownStation() {
        // when
        지하철_노선에_지하철_구간_생성_요청(이호선, createSectionCreateParams(역삼역, 선릉역));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(이호선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 역삼역, 선릉역);
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 지하철 노선의 마지막 구간 제거를 요청 하면
     * Then 노선에 구간이 제거된다
     */
    @DisplayName("지하철 노선에 마지막 구간을 제거")
    @Test
    void removeLastLineSection() {
        // given
        지하철_노선에_지하철_구간_생성_요청(이호선, createSectionCreateParams(역삼역, 선릉역));

        // when
        지하철_노선에_지하철_구간_제거_요청(이호선, 선릉역);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(이호선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 역삼역);
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청하고
     * When 지하철 노선의 중간 구간 제거를 요청하면
     * Then 노선에 구간이 제거된다.
     */
    @DisplayName("지하철 노선의 중간 구간을 제거")
    @Test
    void removeMiddleSection() {
        // given
        지하철_노선에_지하철_구간_생성_요청(이호선, createSectionCreateParams(역삼역, 선릉역));

        // when
        지하철_노선에_지하철_구간_제거_요청(이호선, 역삼역);

        // then
        final ExtractableResponse<Response> response = 지하철_노선_조회_요청(이호선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 선릉역);
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청하고
     * When 지하철 노선의 처음 구간 제거를 요청하면
     * Then 노선에 구간이 제거된다.
     */
    @DisplayName("지하철 노선의 중간 구간을 제거")
    @Test
    void removeFirstSection() {
        // given
        지하철_노선에_지하철_구간_생성_요청(이호선, createSectionCreateParams(역삼역, 선릉역));

        // when
        지하철_노선에_지하철_구간_제거_요청(이호선, 강남역);

        // then
        final ExtractableResponse<Response> response = 지하철_노선_조회_요청(이호선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(역삼역, 선릉역);
    }

    private Map<String, String> createLineCreateParams(Long upStationId, Long downStationId) {
        Map<String, String> lineCreateParams;
        lineCreateParams = new HashMap<>();
        lineCreateParams.put("name", "2호선");
        lineCreateParams.put("color", "bg-green-600");
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
}
