package nextstep.subway.unit;

import java.util.List;
import nextstep.subway.line.Line;
import nextstep.subway.line.LineRepository;
import nextstep.subway.line.LineService;
import nextstep.subway.section.Section;
import nextstep.subway.station.Station;
import nextstep.subway.station.StationRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

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
        Station upStation = new Station("상행역");
        Station downStation = new Station("해행역");
        stationRepository.save(upStation);
        stationRepository.save(downStation);

        Line saveLine = new Line("2호선", "green", 10L);
        lineRepository.save(saveLine);

        Line findLine = lineService.findLineById(1L);
        Section section = new Section(findLine, upStation, downStation, 10L);

        // when
        // lineService.addSection 호출
        lineService.addSection(findLine, section);

        // then
        // findLine.getSections 메서드를 통해 검증
        List<Section> sections = findLine.getSections();
        Assertions.assertThat(sections.size()).isEqualTo(1);
    }
}
