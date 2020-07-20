package nextstep.subway.path.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineStation;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class PathFinderTest {
    PathFinder pathFinder;
    private List<Line> lines;

    @BeforeEach
    void setUp() {
        pathFinder = new PathFinder();

        Line line1 = new Line("1호선", "BLUE", LocalTime.of(5, 30), LocalTime.of(23, 30), 10);
        Line line2 = new Line("2호선", "GREEN", LocalTime.of(5, 30), LocalTime.of(23, 30), 10);
        Line line3 = new Line("신분당선", "RED", LocalTime.of(5, 30), LocalTime.of(23, 30), 10);

        line1.addLineStation(new LineStation(1L, null, 10, 10));
        line1.addLineStation(new LineStation(2L, 1L, 10, 10));
        line1.addLineStation(new LineStation(3L, 2L, 10, 10));

        line2.addLineStation(new LineStation(4L, 3L, 10, 10));
        line2.addLineStation(new LineStation(5L, 4L, 10, 10));
        line2.addLineStation(new LineStation(6L, 5L, 10, 10));

        line3.addLineStation(new LineStation(7L, null, 10, 10));

        lines = Lists.newArrayList(line1, line2, line3);
    }

    @Test
    void findShortestPath() {
        // when
        List<LineStation> shortestPath = pathFinder.findShortestPath(lines, 1L, 4L);

        // then
        assertThat(shortestPath).extracting(it -> it.getStationId()).containsExactly(1L, 2L, 3L, 4L);
    }
}
