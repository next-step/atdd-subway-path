package nextstep.subway.unit;

import nextstep.subway.line.LineRepository;
import nextstep.subway.line.entity.Line;
import nextstep.subway.section.SectionCreateRequest;
import nextstep.subway.section.SectionService;
import nextstep.subway.station.Station;
import nextstep.subway.station.StationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class LineServiceTest {
    @Autowired
    private LineRepository lineRepository;
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private SectionService sectionService;

    @Test
    void addSection() {
        // given
        // stationRepository와 lineRepository를 활용하여 초기값 셋팅
        Line 이호선 = new Line("이호선", "green");
        lineRepository.save(이호선);

        Station 잠실역 = new Station("잠실역");
        Station 성수역 = new Station("성수역");
        stationRepository.save(잠실역);
        stationRepository.save(성수역);

        SectionCreateRequest request = new SectionCreateRequest(잠실역.getId(), 성수역.getId(), 10);

        // when
        // lineService.addSection 호출
        sectionService.saveSection(이호선.getId(), request);

        // then
        // line.getSections 메서드를 통해 검증
        assertThat(이호선.getSections().getSections()).hasSize(1);
    }
}
