package nextstep.subway.path.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineStation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PathFinderTest {

    @DisplayName("출발역과 도착역의 최단 거리 경로를 찾는다")
    @Test
    void searchShortestPath() {
        // given
        Line line1 = new Line("2호선", "GREEN", LocalTime.of(6, 30), LocalTime.of(23, 0), 5);
        Line line2 = new Line("신분당선", "RED", LocalTime.of(6, 30), LocalTime.of(23, 0), 5);

        line1.addLineStation(new LineStation(1L, null, 5, 10));
        line1.addLineStation(new LineStation(2L, 1L, 5, 10));
        line1.addLineStation(new LineStation(3L, 2L, 5, 10));

        line2.addLineStation(new LineStation(1L, null, 5, 10));
        line2.addLineStation(new LineStation(4L, 1L, 5, 10));

        List<Line> lines = Arrays.asList(line1, line2);
        PathFinder pathFinder = new PathFinder(lines);

        Long sourceStationId = 3L;
        Long targetStationId = 4L;

        // when
        List<Long> shortestPathStations = pathFinder.searchShortestPath(sourceStationId, targetStationId);

        // then
        assertThat(shortestPathStations.size()).isEqualTo(4);
    }
}
