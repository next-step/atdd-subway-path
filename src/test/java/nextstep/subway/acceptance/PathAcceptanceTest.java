package nextstep.subway.acceptance;

import static nextstep.subway.acceptance.LineSteps.createSectionCreateParams;
import static nextstep.subway.acceptance.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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

        이호선 = 지하철_노선_생성_요청("2호선", "green").jsonPath().getLong("id");
        신분당선 = 지하철_노선_생성_요청("신분당선", "red").jsonPath().getLong("id");
        삼호선 = 지하철_노선_생성_요청("3호선", "orange").jsonPath().getLong("id");

        지하철_노선에_지하철_구간_생성_요청(이호선, createSectionCreateParams(교대역, 강남역, 10));
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 양재역, 10));
        지하철_노선에_지하철_구간_생성_요청(삼호선, createSectionCreateParams(교대역, 남부터미널역, 2));
    }

    @Test
    void test() {
        final ExtractableResponse<Response> 최단_경로_요청 = 최단_경로_요청(교대역, 양재역);

        final List<Long> 역_id_리스트 = 최단_경로_요청.jsonPath().getList("stations.id", Long.class);
        final List<String> 역_이름_리스트 = 최단_경로_요청.jsonPath().getList("stations.name", String.class);
        final long 거리의_합 = 최단_경로_요청.jsonPath().getLong("distance");

        assertThat(역_id_리스트).containsExactly(1L, 4L, 3L);
        assertThat(역_이름_리스트).containsExactly("교대역", "남부터미널역", "양재역");
        assertThat(거리의_합).isEqualTo(5);
    }

    private ExtractableResponse<Response> 최단_경로_요청(final long startStationId, final long endStationId) {
        return RestAssured.given().log().all()
            .params(Map.of(
                "source", startStationId,
                "target", endStationId
            ))
            .when().get("/paths")
            .then().log().all()
            .extract();
    }

}
