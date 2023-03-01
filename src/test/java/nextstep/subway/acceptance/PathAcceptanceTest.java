package nextstep.subway.acceptance;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("지하철 경로 조회 기능")
public class PathAcceptanceTest extends AcceptanceTest{

    /**
     * Given - 지하철 노선/구간/역을 생성하고
     * When - 출발역, 도착역을 지정하여 경로를 조회하면
     * Then - 경로 사이의 역들과 경로 거리를 반환 받을 수 있다.
     */
    @DisplayName("지하철 경로 조회")
    @Test
    void searchPath(){

    }

    /**
     * Given - 지하철 노선/구간/역을 생성하고
     * When - 츨빌역과 도착역을 같게 지정하여 경로를 조회하면
     * Then - 예외를 반환한다.
     */
    @DisplayName("지하철 경로 조회: 출발역 = 도착역")
    @Test
    void searchPathWithSourceSameAsTarget(){

    }

    /**
     * Given - 지하철 노선/구간/역을 생성하고
     * When - 연결되지 않은 출발역과 도착역을 지정하고 경로를 조회하면
     * Then - 예외를 반환한다.
     */
    @DisplayName("지하철 경로 조회: 출발역과 도착역 연결 X")
    @Test
    void searchPathWithSourceTargetDisconnected(){

    }

    /**
     * Given - 지하철 노선/구간/역을 생성하고
     * When - 존재하지 않는 역을 지정하고 경로를 조회하면
     * Then - 예외를 반환한다.
     */
    @DisplayName("지하철 경로 조회: 출발역이 존재하지 않음")
    @Test
    void searchPathFailWithNotExistStation(){

    }
}
