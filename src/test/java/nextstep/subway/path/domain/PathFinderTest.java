package nextstep.subway.path.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PathFinderTest {

    @Test
    void getShortestPath() {
        Line line = new Line("2호선", "green");
        Station upStation = new Station("홍대입구역");
        Station downStation = new Station("신촌역");
        line.addSection(upStation, downStation, 5);
        PathFinder pathFinder = new PathFinder(line);
        assertThat(pathFinder.getShortestDistance(upStation, downStation)).isEqualTo(5);
        assertThat(pathFinder.getShortestPathList(upStation, downStation).size()).isEqualTo(2);
    }
}
