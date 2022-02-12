package nextstep.subway.unit;

import nextstep.subway.domain.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.unit.LineFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ShortestPathFinderTest extends ShortestPathTestableLinesFixture {

    @DisplayName("출발지역과 도착지역의 최단거리를 찾는다")
    @Test
    void findShortestPath() {
        ShortestPathFinder<Station, Line, Integer> shortestPathFinder = new LinesJGraphShortestPathFinder();

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
        ShortestPath<Station, Integer> shortestPath = shortestPathFinder.findShortestPath(강남역, 양재역, lines);

        assertThat(shortestPath.getPaths().stream().map(Station::getId)).containsExactly(2L, 3L, 6L, 5L);
        assertThat(shortestPath.getDistance()).isEqualTo(1100);
    }

    @DisplayName("최단거리를 찾을 때, 출발지역과 도착지역이 같으면 예외가 발생한다")
    @Test
    void findShortestPathSourceAndTargetEqualsFailure() {

        // given
        ShortestPathFinder<Station, Line, Integer> shortestPathFinder = new LinesJGraphShortestPathFinder();

        // when then
        assertThrows(IllegalArgumentException.class, () -> shortestPathFinder.findShortestPath(강남역, 강남역, lines));
    }

    @DisplayName("최단거리를 찾을 때, 출발지역과 도착지역이 연결되어 있지 않으면 예외가 발생한다")
    @Test
    void findShortestPathSourceAndTargetPathNotFoundFailure() {

        // given
        ShortestPathFinder<Station, Line, Integer> shortestPathFinder = new LinesJGraphShortestPathFinder();

        Station 건대입구역 = 역_생성(999L);
        Station 어린이대공원역 = 역_생성(1000L);
        Section 건대입구역과_어린이대공원역_구간 = 구간_생성(999L, 건대입구역, 어린이대공원역, 700);
        Line 노선_7호선 = 상행종점역부터_하행종점역까지_모든구간이_포함된_노선_생성("7호선",
                건대입구역과_어린이대공원역_구간);

        // when then
        assertThrows(IllegalArgumentException.class, () -> shortestPathFinder.findShortestPath(강남역, 건대입구역, lines));
    }
}
