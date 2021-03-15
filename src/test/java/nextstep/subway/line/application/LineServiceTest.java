package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.SectionRequest;
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
        Station 야탑역 = stationRepository.save(new Station("야탑역"));
        Station 이매역 = stationRepository.save(new Station("이매역"));
        Station 서현역 = stationRepository.save(new Station("서현역"));

        Long lineId = lineService.saveLine(
                new LineRequest("라인_1", "YELLOW", 야탑역.getId(), 이매역.getId(), 10)
        ).getId();

        // when
        // lineService.addSection 호출
        lineService.addSection(lineId, new SectionRequest(이매역.getId(), 서현역.getId(), 10));
        Line line = lineRepository.findById(lineId).orElseGet(() -> new Line());

        // then
        // line.getSections 메서드를 통해 검증
        assertThat(line.getSections()).hasSize(2);
    }
}
