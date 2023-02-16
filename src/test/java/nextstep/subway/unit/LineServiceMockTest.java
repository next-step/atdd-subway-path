package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.ui.request.SectionRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        Station 강남역 = new Station(1L, "강남역");
        Station 교대역 = new Station(2L, "교대역");
        Line 이호선 = new Line(1L, "이호선", "green");

        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        when(stationService.findById(강남역.getId())).thenReturn(강남역);
        when(stationService.findById(교대역.getId())).thenReturn(교대역);
        when(lineRepository.findById(이호선.getId())).thenReturn(Optional.of(이호선));
        SectionRequest sectionRequest = new SectionRequest(강남역.getId(), 교대역.getId(), 10);

        // when
        // lineService.addSection 호출
        lineService.addSection(이호선.getId(), sectionRequest);

        // then
        // line.findLineById 메서드를 통해 검증
        verify(lineRepository, times(1)).findById(이호선.getId());
        verify(stationService, times(1)).findById(강남역.getId());
        verify(stationService, times(1)).findById(교대역.getId());
    }
}
