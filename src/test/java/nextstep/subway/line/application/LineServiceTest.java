package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
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
        Station 강남역 = stationRepository.save(new Station("강남역"));
        Station 양재역 = stationRepository.save(new Station("양재역"));
        Station 양재시민의숲 = stationRepository.save(new Station("양재시민의숲"));
        Line 신분당선 = lineRepository.save(new Line("신분당선", "bg-red-600", 강남역, 양재역, 10));

        // when
        // lineService.addSection 호출
        lineService.addSection(신분당선.getId(), new SectionRequest(양재역.getId(), 양재시민의숲.getId(), 10));

        // then
        // line.getSections 메서드를 통해 검증
        assertThat(신분당선.getSections().getFirstStation().getName()).isEqualTo(강남역.getName());
        assertThat(신분당선.getSections().getLastStation().getName()).isEqualTo(양재시민의숲.getName());

    }
}
