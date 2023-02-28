package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static nextstep.subway.acceptance.LineSectionAcceptanceTest.createSectionCreateParams;
import static nextstep.subway.acceptance.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("경로 조회 기능")
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

        이호선 = 지하철_노선_생성_요청("2호선", "green", 교대역, 강남역, 10);
        신분당선 = 지하철_노선_생성_요청("신분당선", "red", 강남역, 양재역, 10);
        삼호선 = 지하철_노선_생성_요청("3호선", "orange", 교대역, 남부터미널역, 2);

        지하철_노선에_지하철_구간_생성_요청(삼호선, createSectionCreateParams(남부터미널역, 양재역, 3));
    }

    /**
     * Given 지하철역, 노선 정보가 주어졌다.
     * When 출발역과 도착역으로 최단 거리 요청을 보낸다.
     * Then 경로를 지나는 역 목록을 응답받는다.
     * Then 거리를 응답받는다.
     */
    @DisplayName("")
    @Test
    void getPath() {
        //When
        ExtractableResponse<Response> response = 경로_요청(교대역, 양재역);

        //Then
        assertThat(response.jsonPath().getList("stations.id", Long.class))
                .containsExactly(1L, 4L, 3L);

        //Then
        assertThat(response.jsonPath().getLong("distance"))
                .isEqualTo(5);
    }

    private ExtractableResponse<Response> 경로_요청(Long sourceId, Long targetId) {
        return RestAssured.given().log().all()
                .when().get("/paths?source={sourceId}&target={targetId}", sourceId, targetId)
                .then().log().all().extract();
    }
}
