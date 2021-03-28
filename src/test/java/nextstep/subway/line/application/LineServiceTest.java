package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.domain.Sections;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        Station 강남역 = new Station("강남역");
        Station 양재역 = new Station("양재역");
        Line 신분당선 = new Line("신분당선", "red");
        stationRepository.save(강남역);
        stationRepository.save(양재역);
        lineRepository.save(신분당선);

        // when
        // lineService.addSection 호출
        lineService.addSection(신분당선, 강남역, 양재역, 100);

        // then
        // line.getSections 메서드를 통해 검증
        Sections sections = 신분당선.getSections();
        Assertions.assertThat(sections.size()).isEqualTo(1);
    }
}
