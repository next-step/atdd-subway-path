package nextstep.subway.acceptance;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("지하철 경로 조회 기능")
public class PathAcceptanceTest extends AcceptanceTest {

    /**
     * when 출발역 ID와 도착역 ID로 지하철 경로조회 요청을 하면
     * then 거리의 합이 가장 작은 경로를 구할 수 있다
     */
    @DisplayName("최단 경로 경로 조회")
    @Test
    void findShortestPath() {
    }

    private void 지하철_경로조회_요청(long 출발역_id, long 도착역_id) {
    }

    /**
     * when 찾을수 없는 출발역 ID와 도착역 ID로 지하철 경로조회 요청을 하면
     * then 경로조회에 실패한다
     */
    @DisplayName("최단 경로 조회 실패")
    @Test
    void failFindShortestPath() {
    }

}
