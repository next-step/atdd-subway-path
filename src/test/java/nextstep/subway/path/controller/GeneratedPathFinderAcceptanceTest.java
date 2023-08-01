package nextstep.subway.path.controller;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.exception.ErrorCode;
import nextstep.subway.station.StationSteps;
import nextstep.subway.utils.ApiTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static nextstep.subway.line.SubwayLineSteps.지하철노선등록요청;
import static nextstep.subway.line.SubwayLineSteps.지하철노선등록요청_생성;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 경로 조회")
class GeneratedPathFinderAcceptanceTest extends ApiTest {

    private Long 교대역, 강남역, 남부터미널역, 양재역, 학동역, 청담역;

    @BeforeEach
    void before() {
        교대역 = 역_생성("교대역");
        강남역 = 역_생성("강남역");
        남부터미널역 = 역_생성("남부터미널역");
        양재역 = 역_생성("양재역");
        지하철노선등록요청(지하철노선등록요청_생성("2호선", "GREEN", 교대역, 강남역, 1));
        지하철노선등록요청(지하철노선등록요청_생성("3호선", "GREEN", 교대역, 남부터미널역, 2));
        지하철노선등록요청(지하철노선등록요청_생성("3호선", "GREEN", 남부터미널역, 양재역, 3));
        지하철노선등록요청(지하철노선등록요청_생성("신분당선", "GREEN", 강남역, 양재역, 3));
    }

    /**
     * <pre>
     *     Feature: 지하철 경로 검색
     *     Scenario: 두 역의 최단 거리 경로를 조회한다.
     *     Given 지하철역이 여러개 추가되어있다.
     *     And 지하철 노선이 여러개 추가되어있다.
     *     And 지하철 노선에 지하철 구간이 여러개 추가되어있다.
     *     When 두 역의 최단 거리 경로를 조회한다.
     *     Then 두 역의 최단 거리 경로를 응답 받는다.
     *     And 두 역의 최단 거리 경로에는 최단 거리가 포함된다.
     *     <p></p>
     *      교대역    --- *2호선* ---   강남역
     *      |                         |
     *    *3호선*                    *신분당선*
     *      |                         |
     *      남부터미널역  --- *3호선* ---  양재
     * </pre>
     */
    @Test
    void 경로조회() {
        // given : 선행조건 기술
        int expectedDistance = 4;
        List<String> expectedStations = List.of("교대역", "강남역", "양재역");

        // when : 기능 수행
        ExtractableResponse<Response> 경로조회응답 = PathSteps.경로조회요청(교대역, 양재역);

        // then : 결과 확인
        경로조회응답_검증(경로조회응답, expectedDistance, expectedStations);
    }

    /**
     *     Given 지하철역이 여러개 추가되어있다.
     *     <p>
     *     When 출발역과 도착역이 같은 경로를 조회한다.
     *     <p>
     *     Then ErrorCode.EQUALS_STATIONS 예외가 발생한다.
     *     <pre>
     *     교대역    --- *2호선* ---   강남역
     *      |                         |
     *    *3호선*                    *신분당선*
     *      |                         |
     *      남부터미널역  --- *3호선* ---  양재
     *      </pre>
     */
    @DisplayName("출발역과 도착역이 같은 경우")
    @Test
    void 출발역과_도착역이_같은_경우_예외_발생() {
        // when : 기능 수행
        ExtractableResponse<Response> 경로조회응답 = PathSteps.경로조회요청(교대역, 교대역);

        // then : 결과 확인
        경로조회응답_출발역과_도착역이_같은경우_예외_발생(경로조회응답);
    }

    /**
     *     Given 지하철역이 여러개 추가되어있다.
     *     <p>
     *     When 출발역과 도착역이 연결이 되어 있지 않은 경로를 조회한다.
     *     <p>
     *     Then ErrorCode.NOT_FOUND_PATH 예외가 발생한다.
     *      <pre>
     *      교대역    --- *2호선* ---   강남역          학동역
     *      |                         |             |
     *      *3호선*                    *신분당선*    *7호선*
     *      |                         |             |
     *      남부터미널역  --- *3호선* ---  양재          청담역
     *      </pre>
     */
    @DisplayName("출발역과 도착역이 연결이 되어 있지 않은 경우")
    @Test
    void 출발역과_도착역이_연결이_되어_있지_않은_경우_예외_발생() {
        // given : 선행조건 기술
        학동역 = 역_생성("학동역");
        청담역 = 역_생성("청담역");
        지하철노선등록요청(지하철노선등록요청_생성("7호선", "GREEN", 학동역, 청담역, 3));

        // when : 기능 수행
        ExtractableResponse<Response> 경로조회응답 = PathSteps.경로조회요청(교대역, 학동역);

        // then : 결과 확인
        경로조회응답_출발역과_도착역이_연결이_되어_있지_않은_경우_예외_발생(경로조회응답);
    }

    /**
     *     Given 지하철역이 여러개 추가되어있다.
     *     <p>
     *     When 존재하지 않은 출발역이나 도착역의 경로를 조회한다.
     *     <p>
     *     Then 예외가 발생한다.
     *     <pre>
     *      교대역    --- *2호선* ---   강남역
     *      |                         |
     *      *3호선*                    *신분당선*
     *      |                         |
     *      남부터미널역  --- *3호선* ---  양재
     *      </pre>
     */
    @DisplayName("존재하지 않은 출발역이나 도착역을 조회 할 경우")
    @Test
    void 존재하지_않은_출발역이나_도착역을_조회_할_경우_예외_발생() {
        // when : 기능 수행
        Long notExistStationId = 9999L;

        ExtractableResponse<Response> 경로조회응답 = PathSteps.경로조회요청(notExistStationId, 교대역);

        // then : 결과 확인
        경로조회응답_존재하지_않은_출발역이나_도착역을_조회_할_경우_예외_발생(경로조회응답);
    }

    private void 경로조회응답_검증(ExtractableResponse<Response> 경로조회응답, int expectedDistance, List<String> expectedStations) {
        assertThat(경로조회응답.statusCode()).isEqualTo(200);
        assertThat(경로조회응답.body().jsonPath().getList("stations")).hasSize(expectedStations.size())
                .extracting("name").containsExactly(expectedStations.toArray());
        assertThat(경로조회응답.body().jsonPath().getLong("distance")).isEqualTo(expectedDistance);
    }

    private void 경로조회응답_출발역과_도착역이_같은경우_예외_발생(ExtractableResponse<Response> 경로조회응답) {
        assertThat(경로조회응답.statusCode()).isEqualTo(400);
        assertThat(경로조회응답.body().jsonPath().getString("message")).isEqualTo(ErrorCode.EQUALS_STATIONS.getMessage());
    }

    private void 경로조회응답_출발역과_도착역이_연결이_되어_있지_않은_경우_예외_발생(ExtractableResponse<Response> 경로조회응답) {
        assertThat(경로조회응답.statusCode()).isEqualTo(400);
        assertThat(경로조회응답.body().jsonPath().getString("message")).isEqualTo(ErrorCode.NOT_FOUND_PATH.getMessage());
    }

    private void 경로조회응답_존재하지_않은_출발역이나_도착역을_조회_할_경우_예외_발생(ExtractableResponse<Response> 경로조회응답) {
        assertThat(경로조회응답.statusCode()).isEqualTo(400);
        assertThat(경로조회응답.body().jsonPath().getString("message")).isEqualTo(ErrorCode.NOT_FOUND_STATION.getMessage());
    }

    private static Long 역_생성(String stationName) {
        return StationSteps.지하철생성요청(StationSteps.지하철생성요청_생성(stationName)).jsonPath().getLong("id");
    }
}