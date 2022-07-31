package nextstep.subway.acceptance;

import io.restassured.RestAssured;
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
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("NonAsciiCharacters")
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

        이호선 = 지하철_노선_생성_요청("2호선", "green", 교대역, 강남역, 10).jsonPath().getLong("id");
        신분당선 = 지하철_노선_생성_요청("신분당선", "red", 강남역, 양재역, 10).jsonPath().getLong("id");
        삼호선 = 지하철_노선_생성_요청("3호선", "orange", 교대역, 남부터미널역, 2).jsonPath().getLong("id");

        지하철_노선에_지하철_구간_생성_요청(삼호선, createSectionCreateParams(남부터미널역, 양재역, 3));
    }

    private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", distance + "");
        return params;
    }

    /**
     * Given 출발역과 도착역이 주어지고
     * When 경로를 조회하면
     * Then 조회한 경로의 역과 최단 구간 거리를 확인할 수 있다
     */
    @DisplayName("경로를 조회")
    @Test
    void findPath() {
        //given
        Long 출발역 = 교대역;
        Long 도착역 = 양재역;

        //when
        ExtractableResponse<Response> response = 지하철_경로조회_요청(출발역, 도착역);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(교대역, 남부터미널역, 양재역);
        assertThat(response.jsonPath().get("distance").equals(5));

    }


    /**
     * Given 출발역과 도착역이 같은 값으로 주어지고
     * When 경로를 조회하면
     * Then 에러가 발생한다
     */
    @DisplayName("출발역과 도착역이 같을 때 에러 발생")
    @Test
    void findPathSourceAndTargetEqualsException() {
        //given
        Long 출발역 = 교대역;
        Long 도착역 = 교대역;

        //when
        ExtractableResponse<Response> response = 지하철_경로조회_요청(출발역, 도착역);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.jsonPath().getString("errorMessage"))
                .isEqualTo("구간 조회 시 출발역과 도착역이 같을 수 없습니다.");

    }


    /**
     * Given 연결되지 않은 구간을 추가하고, 출발역과 도착역이 주어졌을 때
     * When 경로를 조회 시
     * Then 에러가 발생한다
     */
    @DisplayName("출발역과 도착역이 연결되지 않았을 때 에러 발생")
    @Test
    void findPathSourceAndTargetNotLinkedException() {
        //given
        Long 출발역 = 교대역;

        Long 천호역 = 지하철역_생성_요청("천호역").jsonPath().getLong("id");
        Long 강동역 = 지하철역_생성_요청("강동역").jsonPath().getLong("id");

        지하철_노선_생성_요청("오호선", "purple", 천호역, 강동역, 1).jsonPath().getLong("id");

        //when
        ExtractableResponse<Response> response = 지하철_경로조회_요청(출발역, 천호역);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.jsonPath().getString("errorMessage"))
                .isEqualTo("출발역과 도착역이 연결되어 있지 않습니다.");

    }


    /**
     * When 존재하지 않는 출발역으로 경로를 조회 시
     * Then 에러가 발생한다
     */
    @DisplayName("존재하지 않는 출발역으로 경로를 조회 시 에러 발생")
    @Test
    void findPathNotExistSourceStationException() {
        //given
        Long 천호역 = 지하철역_생성_요청("천호역").jsonPath().getLong("id");

        //when
        ExtractableResponse<Response> response = 지하철_경로조회_요청(천호역, 교대역);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.jsonPath().getString("errorMessage"))
                .isEqualTo("출발역 또는 도착역이 존재하지 않습니다.");

    }


    /**
     * When 존재하지 않는 출발역으로 경로를 조회 시
     * Then 에러가 발생한다
     */
    @DisplayName("존재하지 않는 출발역으로 경로를 조회 시 에러 발생")
    @Test
    void findPathNotExistTargetStationException() {
        //given
        Long 천호역 = 지하철역_생성_요청("천호역").jsonPath().getLong("id");

        //when
        ExtractableResponse<Response> response = 지하철_경로조회_요청(교대역, 천호역);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.jsonPath().getString("errorMessage"))
                .isEqualTo("출발역 또는 도착역이 존재하지 않습니다.");

    }

    private ExtractableResponse<Response> 지하철_경로조회_요청(Long sourceId, Long targetId) {
        return RestAssured
                .given().log().all()
                .when().get("/paths?source={sourceId}&target={targetId}", sourceId, targetId)
                .then().log().all().extract();
    }


}
