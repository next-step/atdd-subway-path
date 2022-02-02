package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static nextstep.subway.acceptance.LineSectionSteps.createSectionCreateParams;
import static nextstep.subway.acceptance.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.PathSteps.*;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.*;

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

        이호선 = 지하철_노선_생성_요청("2호선", "green", 교대역, 강남역, 10);
        신분당선 = 지하철_노선_생성_요청("신분당선", "red", 강남역, 양재역, 10);
        삼호선 = 지하철_노선_생성_요청("3호선", "orange", 교대역, 남부터미널역, 2);
        지하철_노선에_지하철_구간_생성_요청(삼호선, createSectionCreateParams(남부터미널역, 양재역, 3));
    }


    /**
     * feature : 최단 경로 조회
     * given : 경로를 검색할 역과 노선을 제공한다.
     * when : 출발지와 도착지를 파라미터로 포함한 최단 경로 조회 요청을 보낸다.
     * then : 최단 경로의 역 순서와 거리를 검증한다.
     */
    @Test
    @DisplayName("최단 경로 조회")
    void shortestPath() {
        //when
        ExtractableResponse<Response> 최단경로 = 최단_경로_조회(교대역, 양재역);

        //then
        최단_경로_검증(최단경로, 3, Arrays.asList("교대역", "남부터미널역", "양재역"), 5);
    }

    /**
     * feature : 최단 경로 조회 _ 예외 케이스 01
     * given : 경로를 검색할 역과 노선을 제공한다.
     * when : 출발지와 도착지를 같은 역으로 최단 경로 조회 요청을 보낸다.
     * then : 경로 조회를 실패한다. (상태 코드와 Error Message 검증)
     */
    @Test
    @DisplayName("최단 경로 조회: 출발지와 도착지를 같은 역")
    void shortestPath_exception_case1() {
        //given
        String message = "출발지와 도착지가 같아 경로를 조회할 수 없습니다.";

        //when
        ExtractableResponse<Response> 최단경로 = 최단_경로_조회(교대역, 교대역);

        //then
        최단_경로_조회_예외_검증(최단경로, message);
    }

    /**
     * feature : 최단 경로 조회 _ 예외 케이스 02
     * given : 현재 노선들과 연결되지 않는 새로운 역과 노선을 생성한다.
     * when : 새로 만든 역을 출발 혹은 도착지로 한 최단 경로 조회 요청을 보낸다.
     * then : 경로 조회를 실패한다. (상태 코드와 Error Message 검증)
     */
    @Test
    @DisplayName("최단 경로 조회: 연결되지 않은 역의 최단경로를 조회한 경우")
    void shortestPath_exception_case2() {
        //given
        String message = "출발지와 도착지가 연결되지 않아서 경로를 조회할 수 없습니다.";
        Long 증산역 = 지하철역_생성_요청("증산역").jsonPath().getLong("id");
        Long 디지털미디어시티역 = 지하철역_생성_요청("디지털미디어시티역").jsonPath().getLong("id");
        Long 육호선 = 지하철_노선_생성_요청("6호선", "brown", 증산역, 디지털미디어시티역, 10);

        //when
        ExtractableResponse<Response> 최단경로 = 최단_경로_조회(교대역, 증산역);

        //then
        최단_경로_조회_예외_검증(최단경로, message);
    }

    /**
     * feature : 최단 경로 조회 _ 예외 케이스 03
     * given : 존재하지 않는 역을 제공한다.
     * when : 존재하지 않는 출발지와 도착지를 파라미터로 포함한 최단 경로 조회 요청을 보낸다.
     * then : 경로 조회를 실패한다. (상태 코드와 Error Message 검증)
     */
    @Test
    @DisplayName("최단 경로 조회: 출발, 도착역에 존재하지 않는 역을 포함한 경우")
    void shortestPath_exception_case3() {
        //given
        Long 디지털미디어시티역 = 101L;
        String message = "존재하지 않은 역의 경로를 조회할 수 없습니다.";

        //when
        ExtractableResponse<Response> 최단경로 = 최단_경로_조회(교대역, 디지털미디어시티역);

        //then
        최단_경로_조회_예외_검증(최단경로, message);
    }
}
