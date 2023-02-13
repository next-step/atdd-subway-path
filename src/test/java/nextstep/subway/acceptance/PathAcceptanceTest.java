package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import nextstep.subway.common.AddTypeEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.acceptance.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;

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
     * 교대역    --- *2호선* ---   강남역
     * |                        |
     * *3호선*                   *신분당선*
     * |                        |
     * 남부터미널역  --- *3호선* ---   양재
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        교대역 = 지하철역_생성_요청("교대역").jsonPath().getLong("id");
        강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");
        남부터미널역 = 지하철역_생성_요청("남부터미널역").jsonPath().getLong("id");

        Map<String, String> 이호선_생성_파라미터 = createLineCreateParams("2호선", "green", 교대역, 강남역, 10);
        Map<String, String> 신분당선_생성_파라미터 = createLineCreateParams("신분당선", "red", 강남역, 양재역, 10);
        Map<String, String> 삼호선_생성_파라미터 = createLineCreateParams("3호선", "orange", 교대역, 남부터미널역, 2);

        이호선 = 지하철_노선_생성_요청(이호선_생성_파라미터).jsonPath().getLong("id");
        신분당선 = 지하철_노선_생성_요청(신분당선_생성_파라미터).jsonPath().getLong("id");
        삼호선 = 지하철_노선_생성_요청(삼호선_생성_파라미터).jsonPath().getLong("id");

        지하철_노선에_지하철_구간_생성_요청(AddTypeEnum.BACK_ADD_SECTION, 삼호선, createSectionCreateParams(남부터미널역, 양재역, 3));
    }

    /**
     * When 지하철 노선에 있는 출발역과 도착역의 ID 값을 가져온다
     * Then 출발역과 도착역 경로를 조회한다.
     */
    @DisplayName("지하철 노선 검색")
    @Test
    void searchPath() {
        var response = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/paths?source={source}&target={target}", 교대역, 양재역)
                .then().log().all().extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(교대역, 남부터미널역, 양재역);
        assertThat(response.jsonPath().getInt("distance")).isEqualTo(5);
    }

    private Map<String, String> createLineCreateParams(String lineName, String lineColor, Long upStationId, Long downStationId, int distance) {
        Map<String, String> lineCreateParams;
        lineCreateParams = new HashMap<>();
        lineCreateParams.put("name", lineName);
        lineCreateParams.put("color", lineColor);
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
