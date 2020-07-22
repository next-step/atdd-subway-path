package nextstep.subway.map.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineStation;
import nextstep.subway.map.dto.PathRequest;
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
        line1.addLineStation(new LineStation(3L, 1L, 100, 100));

        line2.addLineStation(new LineStation(2L, null, 10, 10));
        line2.addLineStation(new LineStation(3L, 2L, 10, 10));

        line3.addLineStation(new LineStation(7L, null, 10, 10));

        lines = Lists.newArrayList(line1, line2, line3);
    }

    @Test
    void findShortestPath() {
        // then
        PathRequest request = new PathRequest(1L, 4L, ShortestPathEnum.DISTANCE.getType());

        // when
        List<LineStation> shortestPath = pathFinder.findShortestPath(lines, request);

        // then
        assertThat(shortestPath).extracting(it -> it.getStationId()).containsExactly(1L, 2L, 3L, 4L);
    }

    @Test
    void findShortestDurationPath() {
        // then
        PathRequest request = new PathRequest(1L, 3L, ShortestPathEnum.DURATION.getType());

        // when
        List<LineStation> shortestPath = pathFinder.findShortestPath(lines, request);
        for(LineStation l : shortestPath) {
            System.out.println(l.getStationId());
        }
        // then
        assertThat(shortestPath).extracting(it -> it.getStationId()).containsExactly(1L, 2L, 3L);
    }
}
