package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.*;
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
        Station 강남역 = stationRepository.save(new Station("강남역"));
        Station 역삼역 = stationRepository.save(new Station("역삼역"));
        Line line = lineRepository.save(new Line("2호선", "bg-green-600"));

        // when
        // lineService.addSection 호출
        SectionRequest sectionRequest = new SectionRequest(강남역.getId(), 역삼역.getId(), 3);
        lineService.addSection(line.getId(), sectionRequest);

        // then
        // line.getSections 메서드를 통해 검증
        assertThat(line.getSections()).containsExactly(new Section(line, 강남역, 역삼역, 3));
    }
}
