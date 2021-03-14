package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.junit.jupiter.api.DisplayName;
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
    @DisplayName("지하철 노선에 지하철 구간을 추가 한다")
    void addSection() {
        // given
        Station 강남역 = stationRepository.save(new Station("강남역"));
        Station 역삼역 = stationRepository.save(new Station("역삼역"));
        Station 삼성역 = stationRepository.save(new Station("삼성역"));
        Line 이호선 = lineRepository.save(new Line("2호선", "green", 강남역, 역삼역, 10));

        // when
        이호선.addSection(역삼역, 삼성역, 15);

        // then
        Line line = lineService.findLineById(이호선.getId());
        assertThat(line.getSections().size()).isEqualTo(2);
    }
}
