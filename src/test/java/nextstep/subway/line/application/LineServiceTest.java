package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
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

    private Station 강남역;
    private Station 교대역;
    private Station 삼성역;
    private Line line;

    @BeforeEach
    void setUp() {
        강남역 = stationRepository.save(new Station("강남역"));
        교대역 = stationRepository.save(new Station("교대역"));
        삼성역 = stationRepository.save(new Station("삼성역"));
        line = lineRepository.save(new Line("이호선", "green", 강남역, 교대역, 10));
    }

    @Test
    void addSection() {
        // when
        // lineService.addSection 호출
        lineService.addSection(line, 교대역.getId(), 삼성역.getId(), 20);
        // then
        // line.getSections 메서드를 통해 검증
        assertThat(line.getSections().getSections()).hasSize(2);
    }

    @Test
    void addSectionBetweenStations() {
        // when
        // lineService.addSection 호출
        lineService.addSection(line, 삼성역.getId(), 교대역.getId(), 5);
        // then
        // line.getSections 메서드를 통해 검증
        assertThat(line.getSections().getSections()).hasSize(2);
    }

    @Test
    void removeLateSection() {
        // given
        lineService.addSection(line, 교대역.getId(), 삼성역.getId(), 20);

        // when
        lineService.removeSection(line.getId(), 삼성역.getId());

        // then
        // line.getSections 메서드를 통해 검증
        assertThat(line.getSections().getSections()).hasSize(1);
    }

    @Test
    void removeMiddleSection() {
        // given
        // stationRepository와 lineRepository를 활용하여 초기값 셋팅
        int distance = 20;
        lineService.addSection(line, 교대역.getId(), 삼성역.getId(), distance);

        // when
        lineService.removeSection(line.getId(), 교대역.getId());

        // then
        // line.getSections 메서드를 통해 검증
        assertThat(line.getSections().getSections()).hasSize(1);
        assertThat(line.getSections().getDistances().get(0)).isEqualTo(distance + 10);
    }
}
