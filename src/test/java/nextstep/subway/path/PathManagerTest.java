package nextstep.subway.path;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import java.util.HashMap;
import java.util.List;
import nextstep.subway.line.Line;
import nextstep.subway.section.Section;
import nextstep.subway.station.Station;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

public class PathManagerTest {
    @Test
    public void testMakeGraph() {
        // given
        Station gyodae = new Station(1L);
        Station nambuTerminal = new Station(2L);
        Station gangnam = new Station(3L);

        Line line3 = new Line("3호선", "3호선_색상");
        line3.addSection(new Section(gyodae.getId(), nambuTerminal.getId(), 10L));
        Line line2 = new Line("2호선", "2호선_색상");
        line2.addSection(new Section(gyodae.getId(), gangnam.getId(), 10L));

        HashMap<Line, List<Station>> map = new HashMap<>();
        map.put(line2, List.of(gyodae, gangnam));
        map.put(line3, List.of(gyodae, nambuTerminal));

        // when
        WeightedMultigraph<Station, DefaultWeightedEdge> result = PathManager.makeGraph(map);

        // then
        assertTrue(result.containsVertex(gyodae));
        assertTrue(result.containsVertex(nambuTerminal));
        assertTrue(result.containsVertex(gangnam));

        DefaultWeightedEdge edge1 = result.getEdge(gyodae, nambuTerminal);
        DefaultWeightedEdge edge2 = result.getEdge(gangnam, gyodae);
        assertThat(edge1).isNotNull();
        assertThat(edge2).isNotNull();

        assertThat(result.getEdgeWeight(edge1)).isEqualTo(10);
        assertThat(result.getEdgeWeight(edge2)).isEqualTo(10);
    }
}
