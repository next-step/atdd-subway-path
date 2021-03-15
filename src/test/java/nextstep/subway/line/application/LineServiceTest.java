package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

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
    private Station 삼성역;
    private Station 교대역;
    private Line 이호선;

    @BeforeEach
    void setUp() {
        강남역 = new Station("강남역");
        삼성역 = new Station("삼성역");
        교대역 = new Station("교대역");
        이호선 = new Line("2호선", "green", 강남역, 삼성역, 10);
    }

    @Test
    void addSection() {
        // given
        // stationRepository와 lineRepository를 활용하여 초기값 셋팅
        LineService lineService = new LineService(lineRepository, new StationService(stationRepository));

        // when
        // lineService.addSection 호출
        lineService.addSection(이호선, 삼성역, 교대역, 20);

        // then
        // line.getSections 메서드를 통해 검증
        Assertions.assertThat(이호선.getSections()).hasSize(2);
        Assertions.assertThat(이호선.getSections().get(0).getDownStation()).isEqualTo(삼성역);
    }
}
