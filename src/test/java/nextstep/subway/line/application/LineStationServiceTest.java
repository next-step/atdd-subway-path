package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.LineStation;
import nextstep.subway.line.dto.LineStationCreateRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.assertj.core.api.Assertions;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@DisplayName("지하철 노선 서비스 테스트")
@ExtendWith(MockitoExtension.class)
public class LineStationServiceTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationRepository stationRepository;
    @Mock
    private Line line;
    @Mock
    private Station station;
    @Mock
    private Station preStation;

    private LineStationService lineStationService;

    @BeforeEach
    void setUp() {
        lineStationService = new LineStationService(lineRepository, stationRepository);
    }

    @DisplayName("지하철 노선에 역을 등록한다.")
    @Test
    void addLineStation1() {
        //given
        when(station.getId()).thenReturn(2L);
        when(preStation.getId()).thenReturn(1L);
        when(lineRepository.findById(anyLong())).thenReturn(Optional.of(line));
        when(stationRepository.findAllById(anyList())).thenReturn(Lists.newArrayList(station, preStation));

        //when
        lineStationService.addLineStation(1L, new LineStationCreateRequest(2L, 1L, 5, 5));

        //then
        verify(line).addLineStation(any(LineStation.class));
    }

    @DisplayName("존재하지 않는 역을 등록한다.")
    @Test
    void addLineStation2() {
        //given
        when(station.getId()).thenReturn(1L);
        when(lineRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(stationRepository.findAllById(anyList())).thenReturn(Lists.newArrayList(station));

        //when
        assertThatThrownBy(() -> lineStationService.addLineStation(1L, new LineStationCreateRequest(1L, null, 5, 5)))
                .isInstanceOf(RuntimeException.class);
    }

    @DisplayName("지하철 노선에 역을 제외한다.")
    @Test
    void removeLineStation() {
        //given
        when(lineRepository.findById(anyLong())).thenReturn(Optional.of(line));

        //when
        lineStationService.removeLineStation(1L, 1L);

        //then
        verify(line).removeLineStationById(1L);
    }
}
