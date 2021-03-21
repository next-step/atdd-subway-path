package nextstep.subway.path.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class PathFinderTest {

    Line line;
    Station upStation;
    Station downStation;

    @BeforeEach
    void setUp() {
        line = new Line("2호선", "green");
        upStation = new Station("홍대입구역");
        downStation = new Station("신촌역");
        line.addSection(upStation, downStation, 5);
    }

    @DisplayName("연결된 두 역간 최단경로/거리 조회")
    @Test
    void getPath() {
        PathFinder pathFinder = new PathFinder(Arrays.asList(line));
        assertThat(pathFinder.getShortestDistance(upStation, downStation)).isEqualTo(5);
        assertThat(pathFinder.getShortestPathList(upStation, downStation).size()).isEqualTo(2);
    }

    @DisplayName("출발역과 도착역이 같은 경우")
    @Test
    void getPath_sameSourceAndTarget() {
        PathFinder pathFinder = new PathFinder(Arrays.asList(line));
        assertThatThrownBy(() -> pathFinder.getShortestDistance(upStation, upStation)).isInstanceOf(RuntimeException.class);
    }

    @DisplayName("출발역과 도착역이 연결이 되어 있지 않은 경우")
    @Test
    void getPath_unconnectedSourceAndTarget() {
        Line lineNumberThree = new Line("1호선", "blue");
        Station newUpStation = new Station("시청역");
        Station newDownStation = new Station("서울역");
        lineNumberThree.addSection(newUpStation, newDownStation, 7);

        PathFinder pathFinder = new PathFinder(Arrays.asList(line, lineNumberThree));
        assertThatThrownBy(() -> pathFinder.getShortestDistance(upStation, newUpStation)).isInstanceOf(RuntimeException.class);
    }

    @DisplayName("존재하지 않는 출발역이나 도착역을 조회")
    @Test
    void getPath_unregisteredSource() {
        PathFinder pathFinder = new PathFinder(Arrays.asList(line));
        Station unregisteredStation = new Station("공덕역");
        assertThatThrownBy(() -> pathFinder.getShortestDistance(upStation, unregisteredStation)).isInstanceOf(RuntimeException.class);

    }

}
