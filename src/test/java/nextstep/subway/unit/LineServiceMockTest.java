package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@DisplayName("구간 서비스 단위 테스트 with Mock")
@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {

    @Mock
    private LineRepository lineRepository;

    @Mock
    private StationService stationService;

    @InjectMocks
    private LineService lineService;

    @DisplayName("지하철 노선에 구간 추가")
    @Test
    void addSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        Station 강남역 = new Station("강남역");
        Station 양재역 = new Station("양재역");
        Line 이호선 = new Line("2호선", "green");

        Mockito.when(stationService.findById(강남역.getId())).thenReturn(강남역);
        Mockito.when(stationService.findById(양재역.getId())).thenReturn(양재역);
        Mockito.when(lineRepository.findById(이호선.getId())).thenReturn(Optional.of(이호선));

        // when
        // lineService.addSection 호출
        SectionRequest sectionRequest = new SectionRequest(강남역.getId(), 양재역.getId(), 7);
        lineService.addSection(이호선.getId(), sectionRequest);

        // then
        Mockito.verify(lineRepository).findById(이호선.getId());
    }
}
