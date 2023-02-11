package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.line.Line;
import nextstep.subway.domain.line.LineRepository;
import nextstep.subway.domain.section.Section;
import nextstep.subway.domain.station.Station;
import nextstep.subway.domain.station.StationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class LineServiceTest {
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private LineService lineService;

    @Test
    void addSection() {
        // given
        // stationRepository와 lineRepository를 활용하여 초기값 셋팅
        stationRepository.save(new Station("up"));
        stationRepository.save(new Station("down"));
        lineRepository.save(new Line("name", "color"));
        SectionRequest sectionRequest = new SectionRequest(1L, 2L, 20);

        // when
        // lineService.addSection 호출
        lineService.addSection(1L, sectionRequest);

        // then
        // line.getSections 메서드를 통해 검증
        Line line = lineRepository.findById(1L).get();
        assertThat(line.getSections())
                .extracting(Section::getUpStation)
                .extracting(Station::getId)
                .containsExactly(1L);
        assertThat(line.getSections())
                .extracting(Section::getDownStation)
                .extracting(Station::getId)
                .containsExactly(2L);
    }
}
