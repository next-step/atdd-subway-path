package atdd.station.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SubwayLineTest {

    @DisplayName("지하철 노선에 포함된 지하철역 정보들을 조회한다.")
    @Test
    void name() {
        // given
        SubwayLine subwayLine = SubwayLine.of("2호선");
        subwayLine.setSubwaySections(Arrays.asList(SubwaySection.of(subwayLine, Station.of(3L, "몽촌토성역"), Station.of(4L, "잠실역"))
                , SubwaySection.of(subwayLine, Station.of(2L, "강동구청역"), Station.of(3L, "몽촌토성역"))
                , SubwaySection.of(subwayLine, Station.of(1L, "천호역"), Station.of(2L, "강동구청역"))));

        // when
        List<Station> stations = subwayLine.getStations();

        // then
        assertThat(stations).hasSize(4);
        assertThat(stations).containsExactly(
                Station.of(1L, "천호역")
                , Station.of(2L, "강동구청역")
                , Station.of(3L, "몽촌토성역")
                , Station.of(4L, "잠실역"));
    }

}
