package atdd.station.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static atdd.station.fixture.StationFixture.KANGNAM_STATION;
import static atdd.station.fixture.StationFixture.YUCKSAM_STATON;
import static org.assertj.core.api.Assertions.assertThat;

public class GraphTest {

    @DisplayName("시작_Id_와_종료_ID_로_최단경로를_구할수_있는지")
    @Test
    public void  createSubwayGraph() {
        //given
        long startId = 0L;
        long endId = 5L;
        Graph graph = new Graph;

        //when
        List<Station> result = graph.getShortestDistancePath(startId, endId);

        //then
        assertThat(result.get(0)).isEqualTo(KANGNAM_STATION);
        assertThat(result.get(1)).isEqualTo(YUCKSAM_STATON);
    }
}
