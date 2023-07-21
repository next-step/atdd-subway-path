package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.ui.LineController;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.Mockito.mock;

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
        Station upStation = new Station("강남역");
        Station downStation = new Station("양재역");
        stationRepository.save(upStation);
        stationRepository.save(downStation);

        Line line = new Line("2호선", "green");
        lineRepository.save(line);

        // when
        // lineService.addSection 호출
        SectionRequest sectionRequest = new SectionRequest(upStation.getId(), downStation.getId(), 7);
        lineService.addSection(line.getId(), sectionRequest);

        // then
        // line.getSections 메서드를 통해 검증
        Assertions.assertThat(line.getSections()).hasSize(1);
    }

    @Test
    void saveLine() {
        // given
        Station upStation = new Station("강남역");
        Station downStation = new Station("양재역");
        stationRepository.save(upStation);
        stationRepository.save(downStation);
        LineRequest lineRequest = new LineRequest("2호선", "green", 1L, 2L, -1);

        // when
        lineService.saveLine(lineRequest);

    }
}
