package nextstep.subway.path.acceptance;

import nextstep.subway.AcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class PathAcceptanceTest extends AcceptanceTest {

    @BeforeEach
    public void setUp() {
        super.setUp();
    }

    @DisplayName("최단거리 조회 성공")
    @Test
    void getShortestPath() {
    }

    @DisplayName("출발역과 도착역이 같음")
    @Test
    void getShortestPathWithSameStation() {
    }

    @DisplayName("연결되어 있지 않은 역 간의 최단거리 조회")
    @Test
    void getShortestPathWithDisconnectedStation() {
    }

}
