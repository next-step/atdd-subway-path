package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.LineStation;
import nextstep.subway.line.dto.LineStationRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("지하철 노선 서비스 테스트")
@ExtendWith(MockitoExtension.class)
public class LineStationServiceTest {

    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationRepository stationRepository;

    private LineStationService lineStationService;
    private Line line;

    @BeforeEach
    void setUp() {
        line = new Line();
        lineStationService = new LineStationService(lineRepository, stationRepository);
        when(lineRepository.findById(anyLong())).thenReturn(Optional.of(line));
    }

    @DisplayName("지하철 노선에 역을 등록한다.")
    @Test
    void addLineStation1() {
        // given
        Station station = new Station();
        when(stationRepository.findById(anyLong())).thenReturn(Optional.of(station));

        // when
        LineStationRequest lineStationRequest = new LineStationRequest(1L, null, 10, 10);
        lineStationService.addLineStation(1L, lineStationRequest);

        // then
        assertThat(line.getLineStationsInOrder()).hasSize(1);
    }

    @DisplayName("존재하지 않는 역을 등록한다.")
    @Test
    void addLineStation2() {
        // given
        when(lineRepository.findById(anyLong())).thenReturn(Optional.of(line));
        when(stationRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when
        LineStationRequest lineStationRequest = new LineStationRequest(1L, null, 10, 10);

        // then
        assertThatThrownBy(() -> lineStationService.addLineStation(1L, lineStationRequest))
                .isInstanceOf(RuntimeException.class);
    }

    @DisplayName("지하철 노선에 역을 제외한다.")
    @Test
    void removeLineStation() {
    }
}
