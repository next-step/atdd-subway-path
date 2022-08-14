package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.acceptance.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.PathSteps.경로_조회;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("경로 기능")
class PathAcceptanceTest extends AcceptanceTest {
    private Long 신분당선;
    private Long 구호선;
    private Long 강남역;
    private Long 양재역;

    private Long 신논현역;

    private Long 고속터미널역;


    /**
     *        /신분당선/
     *          |
     * /구호선/ - 강남역 - 신논현역 - 고속터미널역
     *           |
     *          양재역
     */

    /**
     * Given 지하철역과 노선 생성을 요청 하고
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");
        신논현역 = 지하철역_생성_요청("신논현역").jsonPath().getLong("id");
        고속터미널역 = 지하철역_생성_요청("고속터미널역").jsonPath().getLong("id");

        Map<String, String> lineCreateParams = createLineCreateParams(강남역, 양재역);
        신분당선 = 지하철_노선_생성_요청(lineCreateParams).jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 양재역));
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(신논현역, 강남역));

        Map<String, String> line9CreateParams = createLineCreateParams(고속터미널역, 신논현역);
        구호선 = 지하철_노선_생성_요청(line9CreateParams).jsonPath().getLong("id");
    }

    /**
     * When 지하철 경로를 요청하면
     * Then 경로가 리턴된다
     */
    @DisplayName("지하철 경로 요청")
    @Test
    void getPath() {
        // when
        ExtractableResponse<Response> response = 경로_조회(양재역, 고속터미널역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(양재역, 강남역, 신논현역, 고속터미널역);
    }

    /**
     * Given 독립적인 새로운 지하철 노선을 생성하고
     * When 독립된 노선과 기존 노선간 경로를 요청하명
     * Then 400에러가 발생한다.
     */
    @DisplayName("독립된 노선 요청시 실패")
    @Test
    void fail_noPathExists() {
        // given
        Long 대림역 = 지하철역_생성_요청("대림역").jsonPath().getLong("id");
        Long 구디역 = 지하철역_생성_요청("구디역").jsonPath().getLong("id");

        Map<String, String> lineCreateParams = createLineCreateParams(구디역, 대림역);
        Long 이호선 = 지하철_노선_생성_요청(lineCreateParams).jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = 경로_조회(강남역, 구디역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * When 존재하지 않는 역을 요청하면
     * Then 400에러가 발생한다.
     */
    @DisplayName("존재하지 않는 역 경로 요청시 실패")
    @Test
    void fail_noStationExists() {
        // given
        Long 대림역 = 지하철역_생성_요청("대림역").jsonPath().getLong("id");
        Long 존재하지_않는_역 = 9999L;


        // when
        ExtractableResponse<Response> response = 경로_조회(대림역, 존재하지_않는_역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
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

    private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", 6 + "");
        return params;
    }
}
