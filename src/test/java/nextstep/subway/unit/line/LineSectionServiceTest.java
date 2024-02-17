package nextstep.subway.unit.line;

import nextstep.subway.line.LineRepository;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.dto.SectionCreateRequest;
import nextstep.subway.line.service.LineSectionService;
import nextstep.subway.station.Station;
import nextstep.subway.station.StationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class LineSectionServiceTest {
    @Autowired
    private LineRepository lineRepository;
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineSectionService lineSectionService;

    @DisplayName("노선에 구간을 등록한다.")
    @Test
    void addSection() {
        // given
        Line 이호선 = new Line("이호선", "green");
        lineRepository.save(이호선);

        Station 잠실역 = new Station("잠실역");
        Station 성수역 = new Station("성수역");
        stationRepository.save(잠실역);
        stationRepository.save(성수역);

        SectionCreateRequest request = new SectionCreateRequest(잠실역.getId(), 성수역.getId(), 10);

        // when
        lineSectionService.saveSection(이호선.getId(), request);

        // then
        assertThat(이호선.getSectionList()).hasSize(1);
    }
}
