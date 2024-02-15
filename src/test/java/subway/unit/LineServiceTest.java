package subway.unit;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import subway.line.Line;
import subway.line.LineRepository;
import subway.line.LineService;
import subway.section.Section;
import subway.section.SectionRequest;
import subway.station.Station;
import subway.station.StationRepository;

import javax.transaction.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
public class LineServiceTest {
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private LineService lineService;

    private final Line lineNumberNine = new Line("9호선", "bg-gold-600");
    private final Station dangsan = new Station("당산역");
    private final Station seonyudo = new Station("선유도역");
    private final Section firstSection = new Section(dangsan, seonyudo, 10L, lineNumberNine);

    @Test
    void addSection() {
        // given
        // stationRepository와 lineRepository를 활용하여 초기값 셋팅
        Station firstStation = stationRepository.save(dangsan);
        Station secondStation = stationRepository.save(seonyudo);
        Line line = lineRepository.save(lineNumberNine);

        SectionRequest sectionRequest = new SectionRequest(1L, 2L, 10L);

        // when
        // lineService.addSection 호출
        lineService.addSection(1L, sectionRequest);

        // then
        // line.getSections 메서드를 통해 검증
        assertThat(line.sections().sections()).hasSize(1);
        assertThat(line.sections().sections()).contains(firstSection);
    }
}
