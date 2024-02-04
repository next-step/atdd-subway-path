package nextstep.subway.line.unit;

import nextstep.subway.line.controller.dto.SectionAddRequest;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.line.service.LineService;
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
        Station 역삼역 = stationRepository.save(new Station("역삼역"));
        Line 이호선 = lineRepository.save(Line.of("2호선", "green"));

        SectionAddRequest request = new SectionAddRequest(강남역.getId(), 역삼역.getId(), 2);

        // when
        // lineService.addSection 호출
        lineService.addSection(이호선.getId(), request);

        // then
        // line.getSections 메서드를 통해 검증
        assertThat(이호선.getAllSections()).isNotEmpty();
        assertThat(이호선.getStations()).contains(강남역, 역삼역);
    }
}
