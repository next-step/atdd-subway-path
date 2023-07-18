package subway.unit.path;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("PathFinder 단위 테스트")
public class PathFinderTest {
    /**
     * Given 구간이 있을 때
     * When 경로 조회를 하면
     * Then 경로 내 역이 목록으로 반환된다
     * Then 경로의 총 길이가 반환된다
     */
    @DisplayName("경로 조회")
    @Test
    void getShortestPath() {

    }
    @DisplayName("경로 조회 : 요청 구간 동일")
    @Test
    void getShortestPathWithSameOrigin() {

    }

    @DisplayName("경로 조회 : 연결되지 않은 구간")
    @Test
    void getShortestPathNotConnectedSection() {

    }

    @DisplayName("경로 조회 : 존재하지 않는 역")
    @Test
    void getShortestPathNotExistStation() {

    }

}
