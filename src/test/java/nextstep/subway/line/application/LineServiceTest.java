package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
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
        Line line = new Line("2호선", "green");
        Station station1 = new Station("홍대입구역");
        Station station2 = new Station("신촌역");

        lineRepository.save(line);
        stationRepository.save(station1);
        stationRepository.save(station2);

        // when
        // lineService.addSection 호출
        lineService.addSection(line.getId(), new SectionRequest(station1.getId(), station2.getId(), 5));

        // then
        // line.getSections 메서드를 통해 검증
        assertThat(line.getSections().size()).isEqualTo(1);
    }
}
