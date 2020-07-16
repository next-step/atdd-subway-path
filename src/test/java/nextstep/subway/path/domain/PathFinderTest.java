package nextstep.subway.path.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineStation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("경로 찾는 PathFinder 도메인에 대한 유닛 테스트")
class PathFinderTest {

    private PathFinder pathFinder;

    @BeforeEach
    void setUp() {

        final Line line2 = new Line("2호선", "GREEN", LocalTime.of(5, 30), LocalTime.of(23, 30), 5);
        line2.addLineStation(new LineStation(1L, null, 10, 10));
        line2.addLineStation(new LineStation(2L, 1L, 10, 10));
        line2.addLineStation(new LineStation(3L, 2L, 10, 10));

        final Line newbd = new Line("신분당선", "RED", LocalTime.of(5, 30), LocalTime.of(23, 30), 5);
        newbd.addLineStation(new LineStation(1L, null, 20, 20));
        newbd.addLineStation(new LineStation(4L, 1L, 20, 20));
        newbd.addLineStation(new LineStation(5L, 4L, 20, 20));

        final List<Line> lines = new ArrayList<>();
        lines.add(line2);
        lines.add(newbd);
        pathFinder = new PathFinder(lines);
    }

    @DisplayName("역 사이의 최단 경로를 탐색한다.")
    @Test
    void find_path() {

        // given
        long srcStationId = 3;
        long dstStationId = 5;

        // when
        final List<Long> shortestPath = pathFinder.getShortestPath(srcStationId, dstStationId);
        final double weight = pathFinder.getShortestPathWeight(srcStationId, dstStationId);

        // then
        assertThat(shortestPath).hasSize(5);
        assertThat(weight).isEqualTo(60);
    }
}