package nextstep.subway.unit;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.util.List;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
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
        Line 이호선 = lineRepository.save(new Line("이호선", "bg-color-green"));
        Station 강남역 = stationRepository.save(new Station("강남역"));
        Station 역삼역 = stationRepository.save(new Station("역삼역"));
        int distance = 10;

        // when
        // lineService.addSection 호출
        lineService.addSection(이호선.getId(), new SectionRequest(강남역.getId(), 역삼역.getId(), distance));

        // then
        // line.getSections 메서드를 통해 검증
        이호선 = lineRepository.findById(이호선.getId()).get();
        List<Section> sections = 이호선.getSections();
        assertThat(sections.get(0).getUpStation().getId()).isEqualTo(강남역.getId());
        assertThat(sections.get(0).getDownStation().getId()).isEqualTo(역삼역.getId());
        assertThat(sections.get(0).getDistance()).isEqualTo(distance);
    }
}
