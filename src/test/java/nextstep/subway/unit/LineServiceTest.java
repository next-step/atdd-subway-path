package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

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
        Station 마들역 = stationRepository.save(new Station("마들역"));
        Station 노원역 = stationRepository.save(new Station("노원역"));
        Line 칠호선 = lineRepository.save(new Line());

        // when
        // lineService.addSection 호출
        lineService.addSection(칠호선.getId(), new SectionRequest(노원역.getId(), 마들역.getId(), 10));

        // then
        // line.getSections 메서드를 통해 검증
        assertThat(칠호선.getSections().size()).isEqualTo(1);
    }
}
