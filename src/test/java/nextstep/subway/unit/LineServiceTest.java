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

    private static final String LINE_ONE_NM = "일호선";
    private static final String LINE_ONE_COLOR = "blue";
    private static final String UP_STATION_NM_= "신도림";
    private static final String DOWN_STATION_NM = "가산디지털단지";
    private static final int DISTANCE_DEFAULT = 10;

    private final Line 일호선 = new Line(LINE_ONE_NM, LINE_ONE_COLOR);
    private final Station 신도림 = new Station(UP_STATION_NM_);
    private final Station 가산디지털단지 = new Station(DOWN_STATION_NM);

    @Test
    void addSection() {
        // given
        // stationRepository와 lineRepository를 활용하여 초기값 셋팅
        Station _신도림 = stationRepository.save(신도림);
        Station _가산디지털단지 = stationRepository.save(가산디지털단지);

        Line _일호선 = lineRepository.save(일호선);

        // when
        // lineService.addSection 호출
        SectionRequest sectionRequest = new SectionRequest(_신도림.getId(), _가산디지털단지.getId(), DISTANCE_DEFAULT);
        lineService.addSection(_일호선.getId(), sectionRequest);

        // then
        // line.getSections 메서드를 통해 검증
        assertThat(_일호선.getSections()).hasSize(1);
    }
}
