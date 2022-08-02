package nextstep.subway.acceptance;

import static nextstep.subway.acceptance.LineSteps.createSectionCreateParams;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@DisplayName("지하철 경로 검색")
class PathAcceptanceTest extends AcceptanceTest {

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
     * When 출발역과 도착역 사이의 경로를 조회하면
     * Then 두 역 사이의 최단경로가 반환된다
     */
    @DisplayName("두 역 사이의 최단경로 조회")
    @Test
    void shortestPath() {
        var response = 최단경로_조회_요청(교대역, 양재역);

        경로와_거리_검증(response, List.of(교대역, 남부터미널역, 양재역), 5);
    }

    /**
     * Given 출발역과 이어지지 않은 구간을 생성하고
     * When 연결되지 않은 역 사이의 경로를 조회하면
     * Then 경로 조회가 실패한다
     */
    @DisplayName("이어지지 않은 역 사이의 경로 조회 실패")
    @Test
    void findingPathFailsWhenStationsAreNotConnected() {
        var 신도림역 = 지하철역_생성_요청("신도림역").jsonPath().getLong("id");
        var 구로역 = 지하철역_생성_요청("구로역").jsonPath().getLong("id");
        지하철_노선_생성_요청("1호선", "blue", 신도림역, 구로역, 2);

        var response = 최단경로_조회_요청(교대역, 구로역);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    /**
     * When 동일한 출발역과 도착역에 대하여 경로를 조회하면
     * Then 경로 조회가 실패한다
     */
    @DisplayName("동일한 출발역과 도착역에 대해 경로 조회 실패")
    @Test
    void findingPathFailsWhenSourceAndTargetStationsAreEquals(){
        var response = 최단경로_조회_요청(교대역, 교대역);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * When 존재하지 않는 역에 대하여 경로를 존재하면
     * Then 경로 조회가 실패한다
     */
    @DisplayName("존재하지 않는 역에 대한 경로 조회 실패")
    @Test
    void findingPathFailsWhenSourceOrTargetStationAreNotExist(){
        var 존재하지_않는_역 = 9876L;
        var response = 최단경로_조회_요청(교대역, 존재하지_않는_역);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private void 경로와_거리_검증(ExtractableResponse<Response> response, List<Long> stations, Integer distance) {
        var stationsResponse = response.jsonPath()
                .getList("stations.id", Long.class);
        var distanceResponse = response.jsonPath().getInt("distance");

        assertAll(
                () -> assertThat(stationsResponse).containsExactlyElementsOf(stations),
                () -> assertThat(distanceResponse).isEqualTo(distance)
        );
    }

    private ExtractableResponse<Response> 최단경로_조회_요청(Long source, Long target) {
        return RestAssured
                .given()
                .queryParam("source", source)
                .queryParam("target", target)
                .log().all()
                .when().get("/paths")
                .then().log().all()
                .extract();
    }

    private Long 지하철_노선_생성_요청(String name, String color, Long upStationId, Long downStationId, Integer distance) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", distance + "");

        return RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all().extract()
                .jsonPath().getLong("id");
    }


}
