package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.DisplayName;
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

    @DisplayName("구간을 정상적으로 추가했습니다")
    @Test
    void addSection() {
        // given
        // stationRepository와 lineRepository를 활용하여 초기값 셋팅
        Station upStation = stationRepository.save(new Station("신논현역"));
        Station downStation = stationRepository.save(new Station("언주역"));
        Line line = lineRepository.save(new Line("9호선", "bg-brown-600"));

        // when
        // lineService.addSection 호출
        lineService.addSection(line.getId(), new SectionRequest(upStation.getId(), downStation.getId(), 10));

        // then
        // line.getSections 메서드를 통해 검증
        assertThat(line.getStations()).containsExactly(upStation, downStation);
    }
}
