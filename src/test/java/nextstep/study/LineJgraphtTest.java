package nextstep.study;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineGraph;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.Test;

import java.util.List;

import static nextstep.subway.utils.LineTestSources.section;
import static nextstep.subway.utils.StationTestSources.station;
import static org.assertj.core.api.Assertions.assertThat;

class LineJgraphtTest {

    @Test
    void 최단거리조회_1구간() {
        final Line line = new Line();
        final Station station1 = station(1);
        final Station station2 = station(2);

        line.addSection(section(station1, station2));

        final LineGraph lineGraph = new LineGraph(line);

        List<Station> result = lineGraph.findShortestPath(station1, station2);
        assertThat(result).hasSize(2);
    }

    @Test
    void 최단거리조회_2구간() {
        final Line line = new Line();
        final Station station1 = station(1);
        final Station station2 = station(2);
        final Station station3 = station(3);

        line.addSection(section(station2, station3));
        line.addSection(section(station1, station2));

        final LineGraph lineGraph = new LineGraph(line);

        List<Station> result = lineGraph.findShortestPath(station1, station3);
        assertThat(result).hasSize(3);
    }

    @Test
    void 최단거리조회_3구간() {
        final Line line = new Line();
        final Station station1 = station(1);
        final Station station2 = station(2);
        final Station station3 = station(3);
        final Station station4 = station(4);

        // when (4, 3), (3, 1), (1, 2) -> (1, 2), (3, 1), (4, 3)
        line.addSection(section(station1, station2));
        line.addSection(section(station3, station1));
        line.addSection(section(station4, station3));

        final LineGraph lineGraph = new LineGraph(line);
        List<Station> result = lineGraph.findShortestPath(station2, station4);

        assertThat(result).hasSize(4);
    }

}
