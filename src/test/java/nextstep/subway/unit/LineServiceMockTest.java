package nextstep.subway.unit;

import nextstep.subway.applicaion.station.StationService;
import nextstep.subway.applicaion.dto.section.SectionRequest;
import nextstep.subway.applicaion.line.LineFinder;
import nextstep.subway.applicaion.line.sections.LineSectionsCUDDoder;
import nextstep.subway.domain.line.Line;
import nextstep.subway.domain.line.LineRepository;
import nextstep.subway.domain.station.Station;
import nextstep.subway.domain.station.StationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationRepository stationRepository;

    @InjectMocks
    private LineSectionsCUDDoder lineSectionCUDDoder;

    @InjectMocks
    private LineFinder lineFinder;


    @Test
    void addSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        when(lineRepository.findById(1L)).thenReturn(Optional.of(new Line("지하철노선1", "bg-red-600")));
        when(stationRepository.findById(any())).thenReturn(Optional.of(new Station("지하철역1"))).thenReturn(Optional.of(new Station("지하철역2")));

        // when
        // lineService.addSection 호출
        lineSectionCUDDoder.addSection(1L, new SectionRequest(1L, 2L, 10));

        // then
        // lineService.findLineById 메서드를 통해 검증
        assertThat(lineFinder.findById(1L).getStations().size()).isEqualTo(2);
    }
}
