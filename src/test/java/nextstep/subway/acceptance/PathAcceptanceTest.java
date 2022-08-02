package nextstep.subway.acceptance;

import static nextstep.subway.acceptance.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("지하철 경로 검색")
public class PathAcceptanceTest extends AcceptanceTest {

    private Long 교대역;
    private Long 강남역;
    private Long 양재역;
    private Long 남부터미널역;

    private Long 이호선;
    private Long 신분당선;
    private Long 삼호선;

    /**
     * 교대역    --- *2호선* ---   강남역 |                        | *3호선*                   *신분당선* |
     * | 남부터미널역  --- *3호선* ---   양재
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        교대역 = 지하철역_생성_요청("교대역").jsonPath().getLong("id");
        강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");
        남부터미널역 = 지하철역_생성_요청("남부터미널역").jsonPath().getLong("id");

        이호선 = 지하철_노선_생성_요청(createLineCreateParams("2호선", "green", 교대역, 강남역, 10)).jsonPath()
            .getLong("id");
        신분당선 = 지하철_노선_생성_요청(createLineCreateParams("신분당선", "red", 강남역, 양재역, 10)).jsonPath()
            .getLong("id");
        삼호선 = 지하철_노선_생성_요청(createLineCreateParams("3호선", "orange", 교대역, 남부터미널역, 2)).jsonPath()
            .getLong("id");

        지하철_노선에_지하철_구간_생성_요청(삼호선, 남부터미널역, 양재역, 3);
    }

    @Test
    @DisplayName("지하철 경로 조회")
    void getLinePath() {
        ExtractableResponse<Response> stations = 지하철_경로_조회(교대역, 양재역);

        assertThat(stations.jsonPath().getList("stations.id", Long.class)).hasSize(3)
            .containsExactly(교대역, 남부터미널역, 양재역);
        assertThat(stations.jsonPath().getLong("distance")).isEqualTo(5);

    }

    @Test
    @DisplayName("지하철 경로 조회-동일역")
    void getLinePathBySameStation() {
        ExtractableResponse<Response> response = 지하철_경로_조회(교대역, 교대역);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("지하철 경로 조회-미연결역")
    void getLinePathToNotConnectedLine() {
        long 녹번역 = 지하철역_생성_요청("녹번역").jsonPath().getLong("id");
        long 구파발역 = 지하철역_생성_요청("구파발역").jsonPath().getLong("id");
        지하철_노선_생성_요청(createLineCreateParams("3호선", "orange", 녹번역, 구파발역, 10)).jsonPath()
            .getLong("id");

        ExtractableResponse<Response> response = 지하철_경로_조회(교대역, 녹번역);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("지하철 경로 조회-미존재역")
    void getLinePathToNotExistingStation() {
        ExtractableResponse<Response> response = 지하철_경로_조회(교대역, 99L);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private Map<String, String> createLineCreateParams(String name, String color, Long upStationId,
        Long downStationId,
        long distance) {
        Map<String, String> lineCreateParams;
        lineCreateParams = new HashMap<>();
        lineCreateParams.put("name", name);
        lineCreateParams.put("color", color);
        lineCreateParams.put("upStationId", upStationId + "");
        lineCreateParams.put("downStationId", downStationId + "");
        lineCreateParams.put("distance", distance + "");
        return lineCreateParams;
    }

    private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId,
        long distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", distance + "");
        return params;
    }

    private ExtractableResponse<Response> 지하철_경로_조회(Long sourceId, Long targetId) {
        return RestAssured.given().log().all()
            .when().get("/paths?source={sourceId}&target={targetId}", sourceId, targetId)
            .then().log().all().extract();
    }

}
