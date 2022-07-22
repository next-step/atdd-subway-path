package nextstep.study;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineGraph;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationPath;
import org.junit.jupiter.api.Test;

import java.util.List;

import static nextstep.subway.utils.LineTestSources.section;
import static nextstep.subway.utils.StationTestSources.station;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LineJgraphtTest {

    @Test
    void 같은역으로최단거리조회시에러발생() {
        final Line line = new Line();
        final Station station1 = station(1);
        final Station station2 = station(2);

        line.addSection(section(station1, station2));

        final LineGraph lineGraph = new LineGraph(List.of(line));
        final IllegalArgumentException result = assertThrows(
                IllegalArgumentException.class,
                () -> lineGraph.findShortestPath(station1, station1));

        assertThat(result).hasMessageContaining("From station and to station is same");
    }

    @Test
    void 최단거리조회_1구간() {
        final Line line = new Line();
        final Station station1 = station(1);
        final Station station2 = station(2);

        line.addSection(section(station1, station2));

        final LineGraph lineGraph = new LineGraph(List.of(line));
        final StationPath result = lineGraph.findShortestPath(station1, station2);

        assertThat(result.numOfStations()).isEqualTo(2);
        assertThat(result.distance()).isEqualTo(10);
    }

    @Test
    void 최단거리조회_2구간() {
        final Line line = new Line();
        final Station station1 = station(1);
        final Station station2 = station(2);
        final Station station3 = station(3);

        line.addSection(section(station2, station3));
        line.addSection(section(station1, station2));

        final LineGraph lineGraph = new LineGraph(List.of(line));
        final StationPath result = lineGraph.findShortestPath(station1, station3);

        assertThat(result.numOfStations()).isEqualTo(3);
        assertThat(result.distance()).isEqualTo(20);
    }

    @Test
    void 최단거리조회_3구간() {
        final Line line = new Line();
        final Station station1 = station(1);
        final Station station2 = station(2);
        final Station station3 = station(3);
        final Station station4 = station(4);

        // when (4, 3), (3, 1), (1, 2) ->  (4, 3) (3, 1), (1, 2)
        line.addSection(section(station1, station2, 3));
        line.addSection(section(station3, station1, 6));
        line.addSection(section(station4, station3, 9));

        final LineGraph lineGraph = new LineGraph(List.of(line));
        final StationPath result = lineGraph.findShortestPath(station2, station4);

        assertThat(result.numOfStations()).isEqualTo(4);
        assertThat(result.distance()).isEqualTo(18);
    }

    @Test
    void 최단거리조회_2노선_3구간() {
        final Line line1 = new Line();
        final Station station1 = station(1);
        final Station station2 = station(2);
        final Station station3 = station(3);
        final Station station4 = station(4);

        // when (4, 3), (3, 1), (1, 2) -> (1, 2), (3, 1), (4, 3)
        line1.addSection(section(station1, station2));
        line1.addSection(section(station2, station3));
        line1.addSection(section(station3, station4));

        final Line line2 = new Line();
        final Station station5 = station(5);

        line2.addSection(section(station1, station5));
        line2.addSection(section(station5, station4));

        final LineGraph lineGraph = new LineGraph(List.of(line1, line2));
        final StationPath result = lineGraph.findShortestPath(station1, station4);

        assertThat(result.numOfStations()).isEqualTo(3);
        assertThat(result.distance()).isEqualTo(20);
    }

}
