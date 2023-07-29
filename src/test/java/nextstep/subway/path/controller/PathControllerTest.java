package nextstep.subway.path.controller;

import nextstep.subway.utils.ApiTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PathControllerTest extends ApiTest {

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
     * </pre>
     */
    @Test
    void 경로조회() {
    }

    /**
     *     Given 지하철역이 여러개 추가되어있다.
     *     <p>
     *     When 출발역과 도착역이 같은 경로를 조회한다.
     *     <p>
     *     Then 예외가 발생한다.
     */
    @DisplayName("출발역과 도착역이 같은 경우")
    @Test
    void 출발역과_도착역이_같은_경우_예외_발생() {
    }

    /**
     *     Given 지하철역이 여러개 추가되어있다.
     *     <p>
     *     When 출발역과 도착역이 연결이 되어 있지 않은 경로를 조회한다.
     *     <p>
     *     Then 예외가 발생한다.
     */
    @DisplayName("출발역과 도착역이 연결이 되어 있지 않은 경우")
    @Test
    void 출발역과_도착역이_연결이_되어_있지_않은_경우_예외_발생() {
    }

    /**
     *     Given 지하철역이 여러개 추가되어있다.
     *     <p>
     *     When 존재하지 않은 출발역이나 도착역의 경로를 조회한다.
     *     <p>
     *     Then 예외가 발생한다.
     */
    @DisplayName("존재하지 않은 출발역이나 도착역을 조회 할 경우")
    @Test
    void 존재하지_않은_출발역이나_도착역을_조회_할_경우_예외_발생() {
    }
}