package nextstep.subway.line.application;

import com.google.common.collect.Lists;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.LineStation;
import nextstep.subway.line.dto.LineStationCreateRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("지하철 노선 서비스 테스트")
public class LineStationServiceTest {
    private LineRepository lineRepository;
    private StationRepository stationRepository;

    private LineStationService lineStationService;

    @BeforeEach
    void setUp() {
        lineRepository = mock(LineRepository.class);
        stationRepository = mock(StationRepository.class);

        lineStationService = new LineStationService(lineRepository, stationRepository);
    }

    @DisplayName("지하철 노선에 역을 등록한다.")
    @Test
    void addLineStation1() {
    }

    @DisplayName("존재하지 않는 역을 등록한다.")
    @Test
    void addLineStation2() {
        // given
        Long stationId = 1L;
        when(stationRepository.findAllById(Lists.newArrayList(null, stationId))).thenReturn(Lists.newArrayList());

        // when
        LineStationCreateRequest lineStationCreateRequest = new LineStationCreateRequest(stationId, null, 2, 2);

        // then
        assertThrows(RuntimeException.class, () -> lineStationService.addLineStation(1L, lineStationCreateRequest));
    }

    @DisplayName("존재하지 않는 역을 이전역으로 등록한다.")
    @Test
    void addLineStation3() {
        // given
        Long preStationId = 1L;
        Long stationId = 2L;
        Station station = new Station("강남역");
        when(stationRepository.findAllById(Lists.newArrayList(preStationId, stationId))).thenReturn(Lists.newArrayList(station));

        // when
        LineStationCreateRequest lineStationCreateRequest = new LineStationCreateRequest(stationId, preStationId, 2, 2);

        // then
        assertThrows(RuntimeException.class, () -> lineStationService.addLineStation(1L, lineStationCreateRequest));
    }

    @DisplayName("지하철 노선에 역을 제외한다.")
    @Test
    void removeLineStation() {
        // given
        Line line = new Line("2호선", "green", LocalTime.of(6, 0), LocalTime.of(23, 0), 5);
        line.addLineStation(new LineStation(1L, null, 2, 2));
        when(lineRepository.findById(anyLong())).thenReturn(Optional.of(line));

        // when
        lineStationService.removeLineStation(1L, 1L);

        // then
        assertThat(line.getLineStations().getLineStations()).isEmpty();
    }
}
