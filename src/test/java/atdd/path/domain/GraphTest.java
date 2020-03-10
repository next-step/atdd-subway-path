package atdd.path.domain;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static atdd.path.TestConstant.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class GraphTest {
    public static final List<Line> LINES = Lists.list(TEST_LINE, TEST_LINE_2, TEST_LINE_3, TEST_LINE_4);
    public static final Graph graph = new Graph(LINES);

    @Test
    void getStationsInShortestPathTest() {
        //given
        Long startId = TEST_STATION.getId();  //from 강남역
        Long endId = TEST_STATION_7.getId();  //to 양재시민의숲역

        //when
        List<Station> stations = graph.getStationsInShortestPath(startId, endId);

        //then
        assertThat(stations.size()).isEqualTo(3); //출발역, 도착역 모두 포함
        assertThat(stations.get(0)).isEqualTo(TEST_STATION);
        assertThat(stations.get(2)).isEqualTo(TEST_STATION_7);
    }
}
