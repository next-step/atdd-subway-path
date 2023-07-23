package nextstep.subway.unit;

import nextstep.subway.domain.*;
import nextstep.subway.exception.InvalidStationException;
import nextstep.subway.exception.NotFoundPathException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PathFinderTest {
    @DisplayName("지하철 경로 조회")
    @Test
    void getPath() {
        // given
        Station station1 = new Station("station1");
        Station station2 = new Station("station2");
        Station station3 = new Station("station3");
        Line line1 = new Line("line1", "red");
        line1.addSection(Section.of(line1, station1, station2, 3));
        Line line2 = new Line("line2", "orange");
        line2.addSection(Section.of(line2, station2, station3, 5));
        Line line3 = new Line("line3", "yellow");
        line3.addSection(Section.of(line3, station1, station3, 9));

        List<Line> lines = Arrays.asList(line1, line2, line3);
        PathFinder pathFinder = PathFinder.of(lines);

        // when
        Path path = pathFinder.getShortestPath(station1, station3);

        // then
        assertThat(path.getStations()).containsExactly(station1, station2, station3);
        assertThat(path.getDistance()).isEqualTo(8);
    }

    @DisplayName("동일한 출발역과 도착역으로 지하철 경로 조회")
    @Test
    void getPathBySameSourceAndTarget() {
        // given
        Station station1 = new Station("station1");
        Station station2 = new Station("station2");
        Station station3 = new Station("station3");
        Line line1 = new Line("line1", "red");
        line1.addSection(Section.of(line1, station1, station2, 3));
        Line line2 = new Line("line2", "orange");
        line2.addSection(Section.of(line2, station2, station3, 5));
        Line line3 = new Line("line3", "yellow");
        line3.addSection(Section.of(line3, station1, station3, 9));

        List<Line> lines = Arrays.asList(line1, line2, line3);
        PathFinder pathFinder = PathFinder.of(lines);

        // when
        assertThrows(InvalidStationException.class, () -> pathFinder.getShortestPath(station1, station1));
    }

    @DisplayName("연결되어 있지 않은 출발역과 도착역으로 지하철 경로 조회")
    @Test
    void getPathByNotConnectedStations() {
        // given
        Station station1 = new Station("station1");
        Station station2 = new Station("station2");
        Station station3 = new Station("station3");
        Station station4 = new Station("station4");
        Line line1 = new Line("line1", "red");
        line1.addSection(Section.of(line1, station1, station2, 3));
        Line line2 = new Line("line2", "orange");
        line2.addSection(Section.of(line2, station3, station4, 5));

        List<Line> lines = Arrays.asList(line1, line2);
        PathFinder pathFinder = PathFinder.of(lines);

        // when
        assertThrows(NotFoundPathException.class, () -> pathFinder.getShortestPath(station1, station3));
    }
}
