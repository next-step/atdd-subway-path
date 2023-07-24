package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;

import nextstep.subway.station.Station;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionTest {

    @DisplayName("구간의 모든 역과 거리를 가중 다중 그래프에 담는다")
    @Test
    void putWeightedMultiGraph() {
        // given
        Station gangnamStation = new Station("강남역");
        Station yangjaeStation = new Station("양재역");
        int GANGNAM_TO_YANGJAE_DISTANCE = 1;
        Section section = new Section(gangnamStation, yangjaeStation, GANGNAM_TO_YANGJAE_DISTANCE);
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);

        // when
        section.putWeightedMultiGraph(graph);

        // then
        DefaultWeightedEdge edge = graph.getEdge(gangnamStation, yangjaeStation);
        assertThat(graph.getEdgeWeight(edge)).isEqualTo(GANGNAM_TO_YANGJAE_DISTANCE);
    }
}
