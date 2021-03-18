package nextstep.subway.line.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.PathResponse;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static nextstep.subway.line.acceptance.LineSteps.지하철_노선_등록되어_있음;
import static nextstep.subway.line.acceptance.LineSteps.지하철_노선에_지하철역_등록_요청;
import static nextstep.subway.station.StationSteps.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("경로 조회 관련 기능 인수 테스트")
public class PathAcceptanceTest extends AcceptanceTest {
    private LineResponse 신분당선;
    private LineResponse 이호선;
    private LineResponse 삼호선;
    private LineResponse 구호선;
    private StationResponse 강남역;
    private StationResponse 양재역;
    private StationResponse 교대역;
    private StationResponse 남부터미널역;
    private StationResponse 봉은사역;
    private StationResponse 석촌역;

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

        강남역 = 지하철역_등록되어_있음("강남역").as(StationResponse.class);
        양재역 = 지하철역_등록되어_있음("양재역").as(StationResponse.class);
        교대역 = 지하철역_등록되어_있음("교대역").as(StationResponse.class);
        남부터미널역 = 지하철역_등록되어_있음("남부터미널역").as(StationResponse.class);
        봉은사역 = 지하철역_등록되어_있음("봉은사역").as(StationResponse.class);
        석촌역 = 지하철역_등록되어_있음("석촌역").as(StationResponse.class);

        신분당선 = 지하철_노선_등록되어_있음("신분당선", "bg-red-600", 강남역, 양재역, 10);
        이호선 = 지하철_노선_등록되어_있음("이호선", "bg-red-600", 교대역, 강남역, 10);
        삼호선 = 지하철_노선_등록되어_있음("삼호선", "bg-red-600", 교대역, 양재역, 5);
        구호선 = 지하철_노선_등록되어_있음("구호선", "bg-red-600", 봉은사역, 석촌역, 15);

        지하철_노선에_지하철역_등록_요청(삼호선, 교대역, 남부터미널역, 3);
    }

    @DisplayName("최단경로를 조회한다.")
    @Test
    void findShortestPath() {
        // given
        Long 강남역Id = 강남역.getId();
        Long 남부터미널역Id = 남부터미널역.getId();

        // when
        ExtractableResponse<Response> response = 지하철역간_최단경로_조회_요청(강남역Id, 남부터미널역Id);

        // then
        지하철역간_최단경로_조회됨(response);
    }

    @DisplayName("최단경로를 조회하다 실패한다 - case1 : 출발역과 도착역이 같은 경우")
    @Test
    void findShortestPathFailedCase1() {
        // given
        Long 강남역Id = 강남역.getId();

        // when
        ExtractableResponse<Response> response = 지하철역간_최단경로_조회_요청(강남역Id, 강남역Id);

        // then
        지하철역간_최단경로_조회_실패됨(response);
    }

    @DisplayName("최단경로를 조회하다 실패한다 - case2 : 출발역과 도착역이 연결이 되어 있지 않은 경우")
    @Test
    void findShortestPathFailedCase2() {
        // given
        Long 강남역Id = 강남역.getId();
        Long 석촌역Id = 석촌역.getId();

        // when
        ExtractableResponse<Response> response = 지하철역간_최단경로_조회_요청(강남역Id, 석촌역Id);

        // then
        지하철역간_최단경로_조회_실패됨(response);
    }

    @DisplayName("최단경로를 조회하다 실패한다 - case3 : 존재하지 않은 출발역이나 도착역을 조회 할 경우")
    @Test
    void findShortestPathFailedCase3() {
        // given
        Long 모르는출발역 = 100L;
        Long 모르는도착역 = 101L;

        // when
        ExtractableResponse<Response> response = 지하철역간_최단경로_조회_요청(모르는출발역, 모르는도착역);

        // then
        지하철역간_최단경로_조회_실패됨(response);
    }

    private ExtractableResponse<Response> 지하철역간_최단경로_조회_요청(Long sourceId, Long targetId) {
        return RestAssured.given().log().all()
                .queryParam("source", sourceId + "")
                .queryParam("target", targetId + "")
                .when()
                .get("/paths")
                .then().log().all()
                .extract();
    }

    private void 지하철역간_최단경로_조회됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.body().jsonPath().getObject(".", PathResponse.class).getDistance()).isEqualTo(12);
    }

    private void 지하철역간_최단경로_조회_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
