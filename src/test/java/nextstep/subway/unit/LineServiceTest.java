package nextstep.subway.unit;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@DisplayName("구간 서비스 단위 테스트")
@Transactional
public class LineServiceTest {
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private LineService lineService;

    private SectionRequest sectionRequest = mock(SectionRequest.class);
    private Line 이호선;
    private Station 강남역;
    private Station 삼성역;

    @DisplayName("지하철노선의 구간을 추가한다.")
    @Test
    void addSection() {
        // given
        // stationRepository와 lineRepository를 활용하여 초기값 셋팅
        이호선 = lineRepository.save(new Line("2호선", "green"));
        강남역 = stationRepository.save(new Station("강남역"));
        삼성역 = stationRepository.save(new Station("삼성역"));
        when(sectionRequest.getUpStationId()).thenReturn(강남역.getId());
        when(sectionRequest.getDownStationId()).thenReturn(삼성역.getId());

        // when
        // lineService.addSection 호출
        lineService.addSection(이호선.getId(), sectionRequest);

        // then
        // line.getSections 메서드를 통해 검증
        Line line = lineRepository.findById(이호선.getId()).orElseThrow();
        Section addedSection = line.getSections().get(0);
        assertThat(addedSection.getLine()).isEqualTo(이호선);
    }
}
