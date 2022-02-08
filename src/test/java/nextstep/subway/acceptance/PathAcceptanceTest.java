package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.domain.Station;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static nextstep.subway.acceptance.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 경로 조회")
class PathAcceptanceTest extends AcceptanceTest {

    private Long 교대역;
    private Long 강남역;
    private Long 양재역;
    private Long 남부터미널역;
    private Long 이호선;
    private Long 신분당선;
    private Long 삼호선;

    /**
     * 교대역    --- *2호선(10m)* ---   강남역
     * |                               |
     * *3호선(2m)*                   *신분당선(10m)*
     * |                               |
     * 남부터미널역  --- *3호선(3m)* ---   양재
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        교대역 = 지하철역_생성_요청("교대역").jsonPath().getLong("id");
        강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");
        남부터미널역 = 지하철역_생성_요청("남부터미널역").jsonPath().getLong("id");

        이호선 = 지하철_노선_생성_요청(createLineCreateParams("2호선", "green", 교대역, 강남역, 10))
                .jsonPath().getLong("id");
        신분당선 = 지하철_노선_생성_요청(createLineCreateParams("신분당선", "red", 강남역, 양재역, 10))
                .jsonPath().getLong("id");
        삼호선 = 지하철_노선_생성_요청(createLineCreateParams("3호선", "orange", 교대역, 남부터미널역, 2))
                .jsonPath().getLong("id");

        지하철_노선에_지하철_구간_생성_요청(삼호선, createSectionCreateParams(남부터미널역, 양재역, 3));
    }

    /**
     * When 출발역과 도착역을 지정하여 조회한다.
     * Then 최단 경로에 포함된 역 정보와 거리를 반환한다.
     */
    @DisplayName("최단 경로 조회")
    @Test
    void getPath() {
        // when
        ExtractableResponse<Response> response = 최단_경로_조회(교대역, 양재역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        List<Long> stations = response.jsonPath().getList("stations.id", Long.class);
        assertThat(stations).containsExactly(교대역, 남부터미널역, 양재역);

        int distance = response.jsonPath().getInt("distance");
        assertThat(distance).isEqualTo(5);
    }

    /**
     * Given 조회할 경로의 출발점, 도착점을 지정한다.
     * When 출발역과 도착역을 같은 역으로 지정하여 조회한다.
     * Then 경로 조회가 실패한다.
     */
    @DisplayName("출발역과 도착역이 같은 경우 조회할 수 없다.")
    @Test
    void exceptionStartAndEndStationDuplication() {
        // given
        Long source = 1L;
        Long target = 1L;

        // when
        ExtractableResponse<Response> response = 최단_경로_조회(source, target);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 조회할 경로의 출발점, 도착점을 지정한다.
     * When 출발역과 도착역이 연결되어 있지 않은 역일 지정하여 조회힌다.
     * Then 경로 조회가 실패한다.
     */
    @DisplayName("출발역과 도착역이 연결되어 있지 않은 경우 조회할 수 없다.")
    @Test
    void exceptionStartAndEndStationNoneConnection() {
        // given
        Long source = 1L;
        Long target = 5L;

        Long 가양역 = 지하철역_생성_요청("가양역").jsonPath().getLong("id");
        Long 증미역 = 지하철역_생성_요청("증미역").jsonPath().getLong("id");

        지하철_노선_생성_요청(createLineCreateParams("9호선", "brown", 가양역, 증미역, 10));

        // when
        ExtractableResponse<Response> response = 최단_경로_조회(source, target);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 조회할 경로의 출발점, 도착점을 지정한다.
     * When 존재하지 않는 역을 조회한다.
     * Then 경로 조회가 실패한다.
     */
    @DisplayName("존재하지 않는 역을 조회할 경우 경로 조회할 수 없다.")
    @Test
    void exceptionStartAndEndNotExistsStation() {
        // given
        Long source = 10L;
        Long target = 20L;

        // when
        ExtractableResponse<Response> response = 최단_경로_조회(source, target);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private Map<String, String> createLineCreateParams(String name, String color, Long upStationId, Long downStationId, int distance) {
        Map<String, String> lineCreateParams;
        lineCreateParams = new HashMap<>();
        lineCreateParams.put("name", name);
        lineCreateParams.put("color", color);
        lineCreateParams.put("upStationId", String.valueOf(upStationId));
        lineCreateParams.put("downStationId", String.valueOf(downStationId));
        lineCreateParams.put("distance", String.valueOf(distance));
        return lineCreateParams;
    }

    private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", String.valueOf(upStationId));
        params.put("downStationId", String.valueOf(downStationId));
        params.put("distance", String.valueOf(distance));
        return params;
    }

    private ExtractableResponse<Response> 최단_경로_조회(Long source, Long target) {
        return RestAssured
                .given().log().all()
                .accept(String.valueOf(ContentType.APPLICATION_JSON))
                .queryParam("source", source)
                .queryParam("target", target)
                .when().get("/paths")
                .then().log().all().extract();
    }
}
