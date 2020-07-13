package nextstep.subway.line.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("지하철 노선 단위 테스트")
public class LineStationsTest {
    private LineStations lineStations;

    @BeforeEach
    void setUp() {
        // given
        lineStations = new LineStations();
        lineStations.add(new LineStation(1L, null, 10, 10));
        lineStations.add(new LineStation(2L, 1L, 10, 10));
        lineStations.add(new LineStation(3L, 2L, 10, 10));
    }

    @DisplayName("지하철 노선에 역을 마지막에 등록한다.")
    @Test
    void add1() {
        // when
        lineStations.add(new LineStation(4L, 3L, 10, 10));

        // then
        List<Long> stationIds = lineStations.getStationsInOrder().stream()
                .map(it -> it.getStationId())
                .collect(Collectors.toList());
        assertThat(stationIds).containsExactly(1L, 2L, 3L, 4L);
    }

    @DisplayName("지하철 노선에 역을 중간에 등록한다.")
    @Test
    void add2() {
        // when
        lineStations.add(new LineStation(4L, 2L, 10, 10));

        // then
        List<Long> stationIds = lineStations.getStationsInOrder().stream()
                .map(it -> it.getStationId())
                .collect(Collectors.toList());
        assertThat(stationIds).containsExactly(1L, 2L, 4L, 3L);
    }

    @DisplayName("이미 등록되어 있던 역을 등록한다.")
    @Test
    void add3() {
        // when
        assertThatThrownBy(() -> lineStations.add(new LineStation(2L, 1L, 10, 10)))
                .isInstanceOf(RuntimeException.class);
    }

    @DisplayName("지하철 노선에 등록된 마지막 지하철역을 제외한다.")
    @Test
    void removeLineStation1() {
        // when
        lineStations.removeByStationId(3L);

        List<Long> stationIds = lineStations.getStationsInOrder().stream()
                .map(it -> it.getStationId())
                .collect(Collectors.toList());
        assertThat(stationIds).containsExactly(1L, 2L);
    }

    @DisplayName("지하철 노선에 등록된 중간 지하철역을 제외한다.")
    @Test
    void removeLineStation2() {
        // when
        lineStations.removeByStationId(2L);

        List<Long> stationIds = lineStations.getStationsInOrder().stream()
                .map(it -> it.getStationId())
                .collect(Collectors.toList());
        assertThat(stationIds).containsExactly(1L, 3L);
    }

    @DisplayName("지하철 노선의 출발점을 제외한다.")
    @Test
    void removeLineStation3() {
        // when
        lineStations.removeByStationId(1L);

        List<Long> stationIds = lineStations.getStationsInOrder().stream()
                .map(it -> it.getStationId())
                .collect(Collectors.toList());
        assertThat(stationIds).containsExactly(2L, 3L);
    }

    @DisplayName("지하철 노선에서 등록되지 않는 역을 제외한다.")
    @Test
    void removeLineStation4() {
        assertThatThrownBy(() -> lineStations.removeByStationId(4L))
                .isInstanceOf(RuntimeException.class);
    }
}
