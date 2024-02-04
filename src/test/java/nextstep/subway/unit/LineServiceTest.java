package nextstep.subway.unit;

import nextstep.subway.dto.SectionRequest;
import nextstep.subway.entity.Line;
import nextstep.subway.entity.Section;
import nextstep.subway.entity.Station;
import nextstep.subway.repository.LineRepository;
import nextstep.subway.repository.StationRepository;
import nextstep.subway.service.LineService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        Long 첫번째_역 = stationRepository.save(new Station("첫번째_역")).getId();
        Long 두번째_역 = stationRepository.save(new Station("두번째_역")).getId();
        Long 세번째_역 = stationRepository.save(new Station("세번째_역")).getId();

        Line 노선 = lineRepository.save(new Line("노선", "빨강", 첫번째_역, 두번째_역, 1));
        노선.createSection(new Section(노선, 첫번째_역, 두번째_역, 1));

        // when
        // lineService.addSection 호출
        lineService.addSection(노선.getId(), new SectionRequest(세번째_역, 두번째_역, 1));

        // then
        // line.getSections 메서드를 통해 검증
        assertThat(노선.getSections().getSize()).isEqualTo(2);
        assertTrue(노선.hasStation(세번째_역));
    }
}
