package atdd.station.model.entity;

import atdd.station.model.Graph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static atdd.TestConstant.*;
import static org.assertj.core.api.Assertions.assertThat;

public class GraphTest {
    private Graph graph;

    @BeforeEach
    void setUp() {
        this.graph = new Graph(Arrays.asList(EDGE_50, EDGE_51, EDGE_52, EDGE_53, EDGE_54, EDGE_55));
    }

    @Test
    void shortestDistancePath() {
        List<Long> pathStationIds = graph.shortestDistancePath(STATION_ID_7, STATION_ID_6);

        assertThat(pathStationIds).hasSize(5);
        assertThat(pathStationIds.get(0)).isEqualTo(STATION_ID_7);
        assertThat(pathStationIds.get(1)).isEqualTo(STATION_ID_3);
        assertThat(pathStationIds.get(2)).isEqualTo(STATION_ID_4);
        assertThat(pathStationIds.get(3)).isEqualTo(STATION_ID_5);
        assertThat(pathStationIds.get(4)).isEqualTo(STATION_ID_6);
    }
}
