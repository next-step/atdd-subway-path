package nextstep.subway.path.domain;

import com.google.common.collect.Lists;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineStation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("ShortestPathFinder")
class ShortestPathFinderTest {
    private List<Line> lines;

    @BeforeEach()
    void setUp() {
        /**
         * 1 - 2
         * |   |
         * 4 ~ 3
         *     ㄴ 5
         *
         * 4 -> 3 으로 가는 구간은 distance 2, duration 1 이고
         * 그 외 다른 구간은 모두 distance 1, duration 2 로 동일하다
         * (1-2-3-5는 distance는 짧고, duration은 길다)
         * (1-4-3-5는 distance는 길고, duration은 짧다)
         */

        Line line1 = new Line("2호선", "GREEN", LocalTime.of(5, 0), LocalTime.of(23, 30), 5);
        line1.addLineStation(new LineStation(1L, 4L, 1, 1));
        line1.addLineStation(new LineStation(2L, 1L, 1, 1));
        line1.addLineStation(new LineStation(3L, 2L, 1, 1));
        line1.addLineStation(new LineStation(4L, 3L, 2, 1));

        Line line2 = new Line("신분당선", "RED", LocalTime.of(5, 0), LocalTime.of(23, 30), 5);
        line2.addLineStation(new LineStation(5L, 3L, 1, 1));

        Line line3 = new Line("1호선", "BLUE", LocalTime.of(5, 0), LocalTime.of(23, 30), 5);
        line3.addLineStation(new LineStation(6L, null, 2, 3));

        lines = Lists.newArrayList(line1, line2, line3);
    }

    @DisplayName("findPath는 지하철역 최단 거리 경로를 검색한다")
    @Test
    void findShortestPath() {
        // when
        List<LineStation> shortestPath = new ShortestPathFinder().findPath(lines, 1L, 5L);

        // then
        assertThat(shortestPath).extracting(it -> it.getStationId()).containsExactly(1L, 2L, 3L, 5L);
    }

    @DisplayName("존재하지 않는 지하철 역에 대한 경로를 검색하면 CannotFindPathException을 던진다")
    @Test
    void whenLineStationNotExists() {
        assertThrows(CannotFindPathException.class, () ->
            new ShortestPathFinder().findPath(lines, 1L, 100L)
        );
    }

    @DisplayName("서로 이어지지 않는 역에 대한 경로를 검색하면 CannotFindPathException을 던진다")
    @Test
    void whenLineStationsAreNotConnected() {
        assertThrows(CannotFindPathException.class, () ->
                new ShortestPathFinder().findPath(lines, 1L, 6L)
        );
    }
}