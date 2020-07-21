package nextstep.subway.path.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineStation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PathFinderTest {

    private PathFinder pathFinder;

    @BeforeEach
    void setUp() {
        // given
        Line line1 = new Line("2호선", "GREEN", LocalTime.of(6, 30), LocalTime.of(23, 0), 5);
        Line line2 = new Line("신분당선", "RED", LocalTime.of(6, 30), LocalTime.of(23, 0), 5);

        line1.addLineStation(new LineStation(1L, null, 5, 10));
        line1.addLineStation(new LineStation(2L, 1L, 5, 10));
        line1.addLineStation(new LineStation(3L, 2L, 5, 10));

        line2.addLineStation(new LineStation(1L, null, 5, 10));
        line2.addLineStation(new LineStation(4L, 1L, 5, 10));

        List<Line> lines = Arrays.asList(line1, line2);
        this.pathFinder = new PathFinder(lines);
    }

    @DisplayName("출발역과 도착역이 이어져 있지 않은 경우 IllegalArgumentException")
    @Test
    void searchShortestPath_notConnected() {
        // when
        assertThatThrownBy(() -> pathFinder.searchShortestPath(1L, 100L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("출발역과 도착역이 같을 경우 경로는 해당 역 한개이다")
    @Test
    void searchShortestPath_sameStation() {
        // when
        List<LineStation> shortestPathLineStations = pathFinder.searchShortestPath(1L, 1L);

        // then
        assertThat(shortestPathLineStations.stream()
                .map(LineStation::getStationId))
                .containsExactly(1L);
    }

    @DisplayName("출발역과 도착역의 최단 거리 경로를 찾는다")
    @Test
    void searchShortestPath() {
        // when
        List<LineStation> shortestPathLineStations = pathFinder.searchShortestPath(3L, 4L);

        // then
        assertThat(shortestPathLineStations.size()).isEqualTo(4);
    }
}
