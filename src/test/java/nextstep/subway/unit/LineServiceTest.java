package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
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
        Station station1 = stationRepository.save(new Station("station1"));
        Station station2 = stationRepository.save(new Station("station2"));
        Line line = lineRepository.save(new Line("line1", "color"));

        // when
        // lineService.addSection 호출
        lineService.addSection(line.getId(), new SectionRequest(station1.getId(), station2.getId(), 10));

        // then
        // line.getSections 메서드를 통해 검증
        assertThat(line.getSections().size()).isEqualTo(1);
    }
}
