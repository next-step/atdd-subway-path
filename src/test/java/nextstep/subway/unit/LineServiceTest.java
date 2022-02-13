package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.*;
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

    /**
     * Given 지하철역을 생성하고 노선도 생성한 후
     * When 노선에 구간 등록을 요청하면
     * Then 노선에 새로운 구간이 추가된다.
     */
    @Test
    void addSection() {
        // given
        // stationRepository와 lineRepository를 활용하여 초기값 셋팅

        final Station 강남역 = stationRepository.save(Station.of("강남역"));
        final Station 역삼역 = stationRepository.save(Station.of("역삼역"));
        final Station 합정역 = stationRepository.save(Station.of("합정역"));
        final Line 이호선 = lineRepository.save(Line.of("이호선", "green", 강남역, 역삼역, Distance.from(100)));

        SectionRequest sectionRequest = new SectionRequest();
        ReflectionTestUtils.setField(sectionRequest, "upStationId", 역삼역.getId());
        ReflectionTestUtils.setField(sectionRequest, "downStationId", 합정역.getId());
        ReflectionTestUtils.setField(sectionRequest, "distance", 10);

        // when
        // lineService.addSection 호출
        lineService.addSection(이호선.getId(), sectionRequest);

        // then
        // line.getSections 메서드를 통해 검증
        assertThat(이호선.getStations().size()).isEqualTo(3);
    }
}
