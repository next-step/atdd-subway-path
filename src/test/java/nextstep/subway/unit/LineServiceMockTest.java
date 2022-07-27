package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

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
        Station upStation = new Station("신논현역");
        Station downStation = new Station("언주역");
        when(stationService.findById(1L)).thenReturn(upStation);
        when(stationService.findById(2L)).thenReturn(downStation);

        Line line = new Line("9호선", "bg-brown-600");
        when(lineRepository.findById(1L)).thenReturn(Optional.of(line));

        // when
        // lineService.addSection 호출
        lineService.addSection(1L, new SectionRequest(1L, 2L, 10));

        // then
        // line.findLineById 메서드를 통해 검증
        Line findLine = lineRepository.findById(1L).orElseThrow(RuntimeException::new);
        assertThat(line.getStations()).containsExactly(upStation, downStation);
    }
}
