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
        final int 거리 = 10;

        final Station 강남역 = stationRepository.save(new Station("강남역"));
        final Station 역삼역 = stationRepository.save(new Station("역삼역"));
        final Line 이호선 = lineRepository.save(new Line("이호선", "green"));

        SectionRequest sectionRequest = new SectionRequest();
        ReflectionTestUtils.setField(sectionRequest, "upStationId", 강남역.getId());
        ReflectionTestUtils.setField(sectionRequest, "downStationId", 역삼역.getId());
        ReflectionTestUtils.setField(sectionRequest, "distance", 거리);

        // when
        // lineService.addSection 호출
        lineService.addSection(이호선.getId(), sectionRequest);

        // then
        // line.getSections 메서드를 통해 검증
        assertThat(이호선.getSections().size()).isEqualTo(1);
    }
}
