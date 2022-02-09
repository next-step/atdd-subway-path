package nextstep.subway.unit;

import nextstep.subway.domain.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ShortestPathFindAlgorithmTest extends ShortestPathTestableLinesFixture {

    @DisplayName("출발지역과 도착지역의 최단거리를 찾는다")
    @Test
    void findShortestPath() {
        ShortestPathFindAlgorithm<Station, Line, Integer> shortestPathFindAlgorithm = new LinesJGraphDijkstraAlgorithm();

        /**
         * 강남역에서 양재역까지의 최단거리 = 1100 m (강남 -> 역삼 -> 판교 -> 양재)
         *
         * 교대역(1)    --- *2호선(700 m)* ---    강남역(2) -> ---*2호선(200 m) --- -> 역삼역(3)
         * |                                         |                                   |
         *                                                                               V
         * *3호선(300 m)*                      *신분당선(2200 m)*                   4호선(800 m)
         * |                                        |                                   |
         *                                                                              V
         * 남부터미널역(4) --- *3호선(300 m)* --- 양재(5) <---- *3호선(100 m)*<--- <- 판교(6)
         */
        ShortestPath<Station, Integer> shortestPath = shortestPathFindAlgorithm.findShortestPath(강남역, 양재역, lines);

        assertThat(shortestPath.getPaths().stream().map(Station::getId)).containsExactly(2L, 3L, 6L, 5L);
        assertThat(shortestPath.getDistance()).isEqualTo(1100);
    }
}
