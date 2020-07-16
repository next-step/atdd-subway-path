package nextstep.subway.line.domain;

import static nextstep.subway.line.domain.LineStationsStep.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
    void 지하철_노선에_역을_마지막에_등록한다() {
        // when
        lineStations.add(new LineStation(4L, 3L, 10, 10));

        // then
        List<Long> stationIds = 지하철역을_순서대로_반환한다(lineStations);
        assertThat(stationIds).containsExactly(1L, 2L, 3L, 4L);
    }

    @DisplayName("지하철 노선에 역을 중간에 등록한다.")
    @Test
    void 지하철_노선에_역을_중간에_등록한다() {
        LineStation lineStation = new LineStation(4L, 2L, 10, 10);
        lineStations.add(lineStation);
        List<Long> stationIds = 지하철역을_순서대로_반환한다(lineStations);
        assertThat(stationIds).containsExactly(1L, 2L, 4L, 3L);
    }

    @DisplayName("이미 등록되어 있던 역을 등록한다.")
    @Test
    void 지하철_노선에_이미_등록된_역을_등록한다() {
        // when
        assertThatThrownBy(() -> lineStations.add(new LineStation(2L, 1L, 10, 10)))
            .isInstanceOf(RuntimeException.class);
    }

    @DisplayName("지하철 노선에 등록된 마지막 지하철역을 제외한다.")
    @Test
    void 지하철_노선에_등록된_마지막_지하철역을_제외한다() {
        lineStations.removeByStationId(3L);
        List<Long> stationIds = 지하철역을_순서대로_반환한다(lineStations);
        assertThat(stationIds).containsExactly(1L, 2L);
    }

    @DisplayName("지하철 노선에 등록된 중간 지하철역을 제외한다.")
    @Test
    void removeLineStation2() {
        lineStations.removeByStationId(2L);
        List<Long> stationIds = 지하철역을_순서대로_반환한다(lineStations);
        assertThat(stationIds).containsExactly(1L, 3L);
    }

    @DisplayName("지하철 노선의 출발점을 제외한다.")
    @Test
    void removeLineStation3() {
        lineStations.removeByStationId(1L);
        List<Long> stationIds = 지하철역을_순서대로_반환한다(lineStations);
        assertThat(stationIds).containsExactly(2L, 3L);
    }

    @DisplayName("지하철 노선에서 등록되지 않는 역을 제외한다.")
    @Test
    void removeLineStation4() {
        assertThatThrownBy(() -> lineStations.removeByStationId(4L))
            .isInstanceOf(RuntimeException.class);
    }
}
