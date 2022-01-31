package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        Line _2호선 = new Line("2호선","green");
        Station 합정역 = stationRepository.save(new Station("합정역"));
        Station 신도림역 = stationRepository.save(new Station("신도림역"));
        Station 홍대입구역 = stationRepository.save(new Station("홍대입구역"));
        _2호선.getSections().add(new Section(_2호선, 합정역, 신도림역, 10));
        lineRepository.save(_2호선);

        SectionRequest sectionRequest = new SectionRequest(신도림역.getId(), 홍대입구역.getId(), 10);

        // when
        // lineService.addSection 호출
        lineService.addSection(_2호선.getId(), sectionRequest);

        // then
        // line.getSections 메서드를 통해 검증
        List<Station> stations = _2호선.getStations();
        assertThat(stations).containsExactly(합정역, 신도림역, 홍대입구역);
    }
}
