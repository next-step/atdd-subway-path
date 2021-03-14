package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
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
        Station 판교역 = stationRepository.save(new Station("판교역"));
        Station 정자역 = stationRepository.save(new Station("정자역"));
        Station 미금역 = stationRepository.save(new Station("미금역"));
        Line 신분당선 = lineRepository.save(new Line("신분당선", "bg-red-600", 판교역, 정자역, 10));

        ReflectionTestUtils.setField(신분당선, "id",1L);
        ReflectionTestUtils.setField(판교역, "id",1L);
        ReflectionTestUtils.setField(정자역, "id",2L);
        ReflectionTestUtils.setField(미금역, "id",3L);

        // when
        // lineService.addSection 호출
        lineService.addSection(신분당선.getId(), new SectionRequest(정자역.getId(), 미금역.getId(), 5));

        // then
        // line.getSections 메서드를 통해 검증
        assertThat(lineService.getStations(신분당선)).contains(판교역,정자역,미금역);
    }
}
