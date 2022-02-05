package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.acceptance.step_feature.StationStepFeature;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Arrays;

import static nextstep.subway.acceptance.step_feature.LineStepFeature.*;
import static nextstep.subway.acceptance.step_feature.PathServiceStepFeature.*;

@DisplayName("지하철 노선 관리 기능")
class PathAcceptanceTest extends AcceptanceTest {

    private StationResponse 강남역;
    private StationResponse 판교역;
    private StationResponse 교대역;

    @BeforeEach
    void setUpStation() {
        강남역 = StationStepFeature.지하철역_생성_조회_요청("강남역");
        판교역 = StationStepFeature.지하철역_생성_조회_요청("판교역");
        교대역 = StationStepFeature.지하철역_생성_조회_요청("교대역");

        지하철_노선_생성_조회_요청(노선_생성_Param_생성("신분당선", 신분당선_색, 강남역.getId(), 판교역.getId(), 30));
        지하철_노선_생성_조회_요청(노선_생성_Param_생성("2호선", "green", 교대역.getId(), 강남역.getId(), 40));

    }

    /**
     * When 가장 짧은 경로를 요청하면
     * Then 짧은 경로의 역들을 순서대로 응답 받는다
     */
    @DisplayName("짧은 경로 조회")
    @Test
    void findShortestPath() {
        // when
        PathResponse response = 최단경로_조회_요청_응답(판교역.getId(), 교대역.getId());

        // then
        최단경로_조회_응답_검증(response, Arrays.asList(판교역.getName(), 강남역.getName(), 교대역.getName()));
    }

    /**
     * When 없는 역을 기준으로 가장 짧은 경로를 요청하면
     * Then 400 status code를 응답한다.
     */
    @DisplayName("없는 역을 조회하면 400응답을 받는다")
    @Test
    void findShortestPath_notFoundStation() {
        // given
        long 없는역Id = 1000;

        // when
        ExtractableResponse<Response> response = 최단경로_조회_요청(판교역.getId(), 없는역Id);

        // then
        최단경로_조회_응답상태_검증(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * When 출발역과 도착역이 같은 경로를 조회 하면
     * Then 400 status code를 응답한다.
     */
    @DisplayName("출발역과 도착역이 같으면 400응답을 받는다")
    @Test
    void findShortestPath_sameStation() {

        // when
        ExtractableResponse<Response> response = 최단경로_조회_요청(판교역.getId(), 판교역.getId());

        // then
        최단경로_조회_응답상태_검증(response, HttpStatus.BAD_REQUEST);
    }

}
