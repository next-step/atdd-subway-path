package nextstep.subway.acceptance;

import org.junit.jupiter.api.DisplayName;

@DisplayName("지하철 경로 조회")
public class PathAcceptanceTest extends AcceptanceTest {

    /**
     * Given 지하철 노선과 구간 생성을 요청하고
     * When 출발역과 도착역의 경로를 조회하면
     * Then 출발역부터 도착역까지 최단거리 경로에 있는 역 목록을 조회할 수 있다.
     */

    /**
     * Given 지하철 노선과 구간 생성을 요청하고
     * When 출발역과 도착역이 같은 경로를 조회하면
     * Then 예외가 발생한다.
     */

    /**
     * Given 지하철 노선과 구간 생성을 요청하고
     * When 출발역과 도착역이 연결되어있지 않은 경로를 조회하면
     * Then 예외가 발생한다.
     */

    /**
     * Given 지하철 노선과 구간 생성을 요청하고
     * When 존재하지 않는 출발역이나 도착역을 조회하면
     * Then 예외가 발생한다.
     */
}
