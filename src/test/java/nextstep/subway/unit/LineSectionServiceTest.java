package nextstep.subway.unit;

import nextstep.subway.line.Line;
import nextstep.subway.line.LineRepository;
import nextstep.subway.linesection.LineSectionService;
import nextstep.subway.station.Station;
import nextstep.subway.station.StationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class LineSectionServiceTest {
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private LineSectionService lineSectionService;

    @Test
    void addSection() {
        // given
        // stationRepository와 lineRepository를 활용하여 초기값 셋팅
        Station 노원역 = stationRepository.save(new Station("노원역"));
        Station 창동역 = stationRepository.save(new Station("창동역"));
        Station 사당역 = stationRepository.save(new Station("사당역"));
        Line line = lineRepository.save(Line.of("4호선", "Ligth-blue", 노원역, 창동역, 10));
        // when
        // lineService.addSection 호출
        lineSectionService.addSection(line.getId(), 창동역.getId(), 사당역.getId(), 10);
        // then
        // line.getSections 메서드를 통해 검증
        Line savedLine = lineRepository.findById(line.getId()).get();
        assertThat(savedLine.getSections().getStations()).containsExactly(노원역, 창동역, 사당역);
    }
}
