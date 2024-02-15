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

    private final Station dangsan = new Station("당산역");
    private final Station seonyudo = new Station("선유도역");
    private final Line line = new Line("9호선", "bg-gold-600");
    private final Section section = new Section(dangsan, seonyudo, 20L, line);

    @Test
    void addSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        LineService lineService = new LineService(lineRepository, stationRepository, sectionRepository);
        SectionRequest sectionRequest = new SectionRequest(1L, 2L, 20L);

        given(lineRepository.findById(1L)).willReturn(Optional.of(line));
        given(stationRepository.findById(1L)).willReturn(Optional.of(dangsan));
        given(stationRepository.findById(2L)).willReturn(Optional.of(seonyudo));

        // when
        // lineService.addSection 호출
        lineService.addSection(1L, sectionRequest);

        // then
        // lineService.findLineById 메서드를 통해 검증
        Line lineAdd = lineRepository.findById(1L).orElseThrow();
        assertThat(lineAdd.sections().sections()).hasSize(1);
        assertThat(lineAdd.sections().sections().get(0)).isEqualTo(section);
    }
}
