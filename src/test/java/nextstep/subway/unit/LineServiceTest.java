package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class LineServiceTest {
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
        Station 삼성역 = stationRepository.save(new Station("삼성역"));
        Line line = new Line("2호선", "green");
        line.addSection(강남역, 역삼역, 10);
        Line 이호선 = lineRepository.save(line);

        // when
        // lineService.addSection 호출
        lineService.addSection(이호선.getId(), new SectionRequest(역삼역.getId(), 삼성역.getId(), 10));

        Line lineResponse = lineService.findById(이호선.getId());

        // then
        // line.getSections 메서드를 통해 검증
        assertThat(lineResponse.getAllStations()).hasSize(3);
    }
}
