package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import java.util.Optional;
import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

    @InjectMocks
    private LineService lineService;

    @Test
    void addSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        Long upStationId = 1L;
        Long downStationId = 2L;
        Long lineId = 1L;
        Station upStation = new Station(upStationId, "강남역");
        Station downStation = new Station(downStationId, "양재역");
        Line line = new Line(1L, "2호선", "green");
        Mockito.when(stationService.findById(upStationId)).thenReturn(upStation);
        Mockito.when(stationService.findById(downStationId)).thenReturn(downStation);
        Mockito.when(lineRepository.findById(lineId)).thenReturn(Optional.of(line));
        SectionRequest request = new SectionRequest(upStationId, downStationId, 10);

        // when
        // lineService.addSection 호출
        lineService.addSection(1L, request);

        // then
        // lineService.findLineById 메서드를 통해 검증
        verify(lineRepository).findById(1L);
        verify(stationService).findById(upStationId);
        verify(stationService).findById(downStationId);
        assertThat(lineService.findById(1L)).isNotNull();
    }
}
