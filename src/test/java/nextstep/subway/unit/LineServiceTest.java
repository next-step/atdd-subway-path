package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.*;
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
        var upStation = stationRepository.save(new Station("신논현역"));
        var downStation = stationRepository.save(new Station("강남역"));
        var line = lineRepository.save(new Line("신분당선", "bg-red-600"));
        line.init(new Section(upStation, downStation, 10));
        var station = stationRepository.save(new Station("양재역"));

        // when
        // lineService.addSection 호출
        var sectionRequest = SectionRequest.of(2L, 3L, 10);
        lineService.addStationToLine(line.getId(), sectionRequest);

        // then
        // line.getSections 메서드를 통해 검증
        assertThat(line.getSections().size()).isEqualTo(2);
    }
}
