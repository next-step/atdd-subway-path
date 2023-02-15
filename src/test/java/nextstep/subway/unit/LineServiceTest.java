package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.ui.request.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

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
        Station 교대역 = stationRepository.save(new Station("교대역"));
        Line 이호선 = lineRepository.save(new Line("이호선", "green"));
        SectionRequest sectionRequest = new SectionRequest(강남역.getId(), 교대역.getId(), 10);

        // when
        // lineService.addSection 호출
        lineService.addSection(이호선.getId(), sectionRequest);

        // then
        // line.getSections 메서드를 통해 검증
        assertAll(() -> {
            assertThat(이호선.getSections()).hasSize(1);
            assertThat(이호선.getAllStations()).containsExactlyElementsOf(List.of(강남역, 교대역));
        });
    }
}
