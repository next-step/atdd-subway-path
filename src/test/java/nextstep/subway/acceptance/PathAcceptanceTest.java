package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.applicaion.constants.ErrorMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.DirtiesContext;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.acceptance.LineSteps.*;
import static nextstep.subway.acceptance.StationSteps.*;
import static org.assertj.core.api.Assertions.assertThat;

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

    /**
     * Given 지하철역과 노선 생성을 요청 하고
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
     * When 출발역과 도착역의 최단 경로를 조회하면
     * Then 두 역 사이의 경로에 있는 역 목록이 조회된다.
     */
    @DirtiesContext
    @DisplayName("경로 조회 - 성공")
    @Test
    void findPath() {
        // when
        ExtractableResponse<Response> response = findPathResponse(교대역, 양재역);

        // then
        assertThat(response.jsonPath().getList("stations.id", Long.class))
                .containsExactly(교대역, 남부터미널역, 양재역);
    }

    /**
     * When 출발역과 도착역이 같으면
     * Then 에러난다
     */
    @DirtiesContext
    @DisplayName("경로 조회 실패 - 출발역과 도착역이 같음")
    @Test
    void findPathBetweenSameStations() {
        // when
        ExtractableResponse<Response> response = findPathResponse(교대역, 교대역);

        // then
        assertThat(response.body().asString()).isEqualTo(ErrorMessage.SAME_BETWEEN_STATIONS);
    }

    /**
     * When 출발역과 도착역이 이어져있지 않으면
     * Then 에러난다
     */
    @DirtiesContext
    @DisplayName("경로 조회 실패 - 출발역과 도착역이 이어져있지 않음")
    @Test
    void findPathNoLinkStations() {
        // given
        Long newStation = 지하철역_생성_요청("송도역").jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = findPathResponse(교대역, newStation);

        // then
        assertThat(response.body().asString()).isEqualTo(ErrorMessage.NO_LINK_TARGET_STATION);
    }

    /**
     * When 출발역이나 도착역이 없으면
     * Then 에러난다
     */
    @DirtiesContext
    @DisplayName("경로 조회 실패 - 경로 없음")
    @Test
    void findPathNoWay() {
        // given
        Long 별내역 = 지하철역_생성_요청("별내역").jsonPath().getLong("id");
        Long 별내별가람역 = 지하철역_생성_요청("별내별가람역").jsonPath().getLong("id");

        Long 사호선 = 지하철_노선_생성_요청(createLineCreateParams("4호선", "파랑", 별내역, 별내별가람역, 10)).jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = findPathResponse(교대역, 별내별가람역);

        // then
        assertThat(response.body().asString()).isEqualTo(ErrorMessage.NOT_FOUND_PATH);
    }

    private ExtractableResponse<Response> findPathResponse(Long sourceId, Long targetId) {
        return RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .queryParam("source", sourceId)
                .queryParam("target", targetId)
                .when().log().all()
                .get("/paths")
                .then().log().all()
                .extract();
    }
    private Map<String, String> createLineCreateParams(String name, String color, Long upStationId, Long downStationId, int distance) {
        Map<String, String> lineCreateParams;
        lineCreateParams = new HashMap<>();
        lineCreateParams.put("name", name);
        lineCreateParams.put("color", color);
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