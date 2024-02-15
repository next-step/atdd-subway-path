package nextstep.subway.unit;

import nextstep.subway.line.api.LineService;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.repository.LineRepository;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.Station;
import nextstep.subway.station.StationRepository;
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
        Station firstStation = stationRepository.save(new Station("firstStation"));
        Station secondStation = stationRepository.save(new Station("secondStation"));
        Line firstLine = lineRepository.save(new Line("firstLine", "blue"));

        Section firstSection = new Section(firstStation.getId(), secondStation.getId(), 10);

        // when
        // lineService.addSection 호출
        lineService.addSection(firstLine, firstSection);

        // then
        // line.getSections 메서드를 통해 검증
        assertThat(firstLine.getSections().getSections().size()).isEqualTo(1);
        assertThat(firstLine.getSections().getStations()).containsAnyOf(firstStation.getId(), secondStation.getId());
    }
}
