package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.domain.PathErrorMessage;
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

public class PathAcceptanceTest extends AcceptanceTest {

    private Long 교대역;
    private Long 강남역;
    private Long 양재역;
    private Long 남부터미널역;
    private Long 천호역;
    private Long 광나루역;
    private Long 런던역 = 999L;
    private Long 이호선;
    private Long 신분당선;
    private Long 삼호선;
    private Long 오호선;

    /**
     * Given 지하철역, 노선, 구간 생성을 요청하고
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        교대역 = 지하철역_생성_요청("교대역").jsonPath().getLong("id");
        강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");
        남부터미널역 = 지하철역_생성_요청("남부터미널역").jsonPath().getLong("id");
        천호역 = 지하철역_생성_요청("천호역").jsonPath().getLong("id");
        광나루역 = 지하철역_생성_요청("광나루역").jsonPath().getLong("id");

        이호선 = 지하철_노선_생성_요청("2호선", "green", 교대역, 강남역, 10).jsonPath().getLong("id");
        신분당선 = 지하철_노선_생성_요청("신분당선", "red", 강남역, 양재역, 10).jsonPath().getLong("id");
        삼호선 = 지하철_노선_생성_요청("3호선", "orange", 교대역, 남부터미널역, 2).jsonPath().getLong("id");
        오호선 = 지하철_노선_생성_요청("5호선", "purple", 광나루역, 천호역, 8).jsonPath().getLong("id");

        지하철_노선에_지하철_구간_생성_요청(삼호선, createSectionCreateParams(남부터미널역, 양재역, 3));
    }

    /**
     * When 교대역에서 강남역으로 가는 경로를 조회하면
     * Then 포함되는 역은 교대역, 남부터미널역, 양재역 총 3개의 역이며
     * Then 거리는 5이다
     */
    @DisplayName("경로를 조회한다")
    @Test
    void findPath() {
        // when
        ExtractableResponse<Response> response = getPathRequest(교대역, 양재역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(교대역, 남부터미널역, 양재역);
        assertThat(response.jsonPath().getInt("distance")).isEqualTo(5);
    }

    /**
     * When 출발역과 도착역이 같은 경로를 조회하면
     * Then 400에러가 발생하고
     * Then 에러 메시지를 응답받는다
     */
    @DisplayName("출발역과 도착역이 같은 경로를 조회한다")
    @Test
    void findPathWithSameTargetAndSource() {
        // when
        ExtractableResponse<Response> response = getPathRequest(교대역, 교대역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        // then
        assertThat(response.body().asString()).isEqualTo(PathErrorMessage.FIND_PATH_SAME_TARGET_AND_SOURCE.getMessage());
    }

    /**
     * When 출발역과 도착역이 연결되어 있지 않은 경로를 조회하면
     * Then 400에러가 발생하고
     * Then 에러 메시지를 응답받는다
     */
    @DisplayName("출발역과 도착역이 연결되어 있지 않은 경로를 조회한다")
    @Test
    void findNotConnectedPath() {
        // when
        ExtractableResponse<Response> response = getPathRequest(교대역, 천호역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        // then
        assertThat(response.body().asString()).isEqualTo(PathErrorMessage.FIND_PATH_NOT_CONNECTED.getMessage());
    }

    /**
     * When 존재하지 않는 역이 포함된 경로를 조회하면
     * Then 400에러가 발생하고
     */
    @DisplayName("존재하지 않는 역이 포함된 경로를 조회")
    @Test
    void findPathWithNonExistentStation() {
        // when
        ExtractableResponse<Response> response = getPathRequest(런던역, 교대역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private ExtractableResponse<Response> getPathRequest(Long sourceId, Long targetId) {
        return RestAssured.given().log().all()
            .queryParam("source", sourceId)
            .queryParam("target", targetId)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().get("/paths")
            .then().log().all()
            .extract();
    }

    private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", distance + "");
        return params;
    }
}
