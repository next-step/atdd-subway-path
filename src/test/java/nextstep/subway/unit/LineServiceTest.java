package nextstep.subway.unit;

import static nextstep.subway.utils.MockString.line2;
import static nextstep.subway.utils.MockString.봉천역;
import static nextstep.subway.utils.MockString.서울대입구역;
import static nextstep.subway.utils.MockString.초록색;
import static org.assertj.core.api.Assertions.assertThat;

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
        Line line = lineRepository.save(new Line(line2, 초록색));
        Station upStation = stationRepository.save(new Station(서울대입구역));
        Station downStation = stationRepository.save(new Station(봉천역));

        // when
        // lineService.addSection 호출
        lineService.addSection(line.getId(), new SectionRequest(upStation.getId(), downStation.getId(), 10));

        // then
        // line.getSections 메서드를 통해 검증
        Line updated = lineRepository.findById(line.getId()).orElseThrow(IllegalArgumentException::new);
        assertThat(updated.getSections().get(0).getUpStation()).isEqualTo(upStation);
        assertThat(updated.getSections().get(0).getDownStation()).isEqualTo(downStation);
    }
}
