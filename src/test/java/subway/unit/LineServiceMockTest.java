package subway.unit;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import subway.line.Line;
import subway.line.LineRepository;
import subway.line.LineService;
import subway.section.Section;
import subway.section.SectionRepository;
import subway.section.SectionRequest;
import subway.station.Station;
import subway.station.StationRepository;
import subway.station.StationService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationRepository stationRepository;
    @Mock
    private SectionRepository sectionRepository;

    @Test
    void addSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        LineService lineService = new LineService(lineRepository, stationRepository, sectionRepository);
        Station upStation = new Station("당산역");
        Station downStation = new Station("선유도역");
        Station thirdStation = new Station("염창역");
        Line line = new Line("9호선", "bg-gold-600", 10L, upStation, downStation);
        SectionRequest sectionRequest = new SectionRequest(2L, 3L, 10L);

        given(lineRepository.findById(1L)).willReturn(Optional.of(line));
        given(stationRepository.findById(1L)).willReturn(Optional.of(upStation));
        given(stationRepository.findById(2L)).willReturn(Optional.of(downStation));
        given(stationRepository.findById(3L)).willReturn(Optional.of(thirdStation));

        // when
        // lineService.addSection 호출
        lineService.addSection(1L, sectionRequest);

        // then
        // lineService.findLineById 메서드를 통해 검증
        Line lineAdd = lineRepository.findById(1L)
                .orElseThrow();
//        assertThat(lineAdd.getSections().getSections().get(0))
//                .isEqualTo(section);
    }
}
