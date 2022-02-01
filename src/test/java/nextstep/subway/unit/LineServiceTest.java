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
        final Station upStation = new Station("upStation");
        final Station downStation = new Station("downStation");
        final Station extraStation = new Station("extraStation");
        final Line line = new Line("name", "color", upStation, downStation, 10);

        stationRepository.save(upStation);
        stationRepository.save(downStation);
        stationRepository.save(extraStation);
        lineRepository.save(line);

        // when
        // lineService.addSection 호출
        lineService.addSection(line.getId(), new SectionRequest(downStation.getId(), extraStation.getId(), 1));

        // then
        // line.getSections 메서드를 통해 검증
        assertThat(line.getStations()).containsExactly(upStation, downStation, extraStation);
    }
}
