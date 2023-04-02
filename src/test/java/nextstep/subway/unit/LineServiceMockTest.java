package nextstep.subway.unit;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

    @InjectMocks
    private LineService lineService;

    @Test
    void addSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        Line 이호선 = new Line("2호선", "bg-color-green");
        Station 강남역 = new Station("강남구청역");
        Station 역삼역 = new Station("역삼역");
        int distance = 10;

        Long 강남역_ID = 강남역.getId();
        Long 역삼역_ID = 역삼역.getId();
        Long 이호선_ID = 이호선.getId();

        when(stationService.findById(강남역_ID)).thenReturn(강남역);
        when(stationService.findById(역삼역_ID)).thenReturn(역삼역);
        when(lineRepository.findById(이호선_ID)).thenReturn(Optional.of(이호선));

        // when
        // lineService.addSection 호출
        lineService.addSection(이호선_ID, new SectionRequest(강남역_ID, 역삼역_ID, distance));

        // then
        // lineService.findLineById 메서드를 통해 검증
        이호선 = lineService.findLineById(이호선_ID);
        List<Section> sections = 이호선.getSections();
        assertThat(sections.get(0).getUpStation().getId()).isEqualTo(강남역_ID);
        assertThat(sections.get(0).getDownStation().getId()).isEqualTo(역삼역_ID);
        assertThat(sections.get(0).getDistance()).isEqualTo(distance);
    }
}
