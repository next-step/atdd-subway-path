package atdd.station.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SubwayMapTest {

    @DisplayName("지하철역 사이의 최단 거리 경로를 조회한다.")
    @Test
    void getShortestPath() {
        // given
        SubwayLine subwayLine = SubwayLine.of("8호선");
        subwayLine.setSubwaySections(Arrays.asList(SubwaySection.of(subwayLine, Station.of(3L, "몽촌토성역"), Station.of(4L, "잠실역"))
                , SubwaySection.of(subwayLine, Station.of(2L, "강동구청역"), Station.of(3L, "몽촌토성역"))
                , SubwaySection.of(subwayLine, Station.of(1L, "천호역"), Station.of(2L, "강동구청역"))));

        SubwayMap subwayMap = new SubwayMap(Collections.singletonList(subwayLine));

        // when
        List<Station> stations = subwayMap.getShortestPath(1L, 4L);

        // then
        assertThat(stations).hasSize(4);
        assertThat(stations).containsExactly(
                Station.of(1L, "천호역")
                , Station.of(2L, "강동구청역")
                , Station.of(3L, "몽촌토성역")
                , Station.of(4L, "잠실역"));
    }

}
