package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.DisplayName;
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
    @DisplayName("지하철 구간을 추가한다.")
    void addSection() {
        // given
        // stationRepository와 lineRepository를 활용하여 초기값 셋팅
        Station 미금역 = stationRepository.save(new Station("미금역"));
        Station 판교역 = stationRepository.save(new Station("판교역"));
        Line 신분당선 = lineRepository.save(new Line("신분당선", "red"));

        // when
        // lineService.addSection 호출
        SectionRequest sectionRequest = new SectionRequest(미금역.getId(), 판교역.getId(), 10);
        lineService.addSection(신분당선.getId(), sectionRequest);

        // then
        // line.getSections 메서드를 통해 검증
        List<Section> sections = 신분당선.getSections();
        assertThat(sections).hasSize(1);

        Section addedSection = sections.get(0);
        assertThat(addedSection.getUpStation()).isEqualTo(미금역);
        assertThat(addedSection.getDownStation()).isEqualTo(판교역);
    }
}
