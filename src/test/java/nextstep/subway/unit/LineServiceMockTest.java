package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LineServiceMockTest {

    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

    LineService lineService = null;

    private Station 가양역;
    private Station 증미역;
    private Long 가양역_ID = 1L;
    private Long 증미역_ID = 2L;
    private Line 구호선;
    private Long 구호선_ID;

    @BeforeEach
    void setUp() {
        가양역 = new Station("가양역");
        증미역 = new Station("증미역");
        가양역_ID = 1L;
        증미역_ID = 2L;

        구호선 = new Line("9호선", "갈색");
        구호선_ID = 1L;

        lineService = new LineService(lineRepository, stationService);
    }

    @Test
    void addSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        when(stationService.findById(가양역_ID)).thenReturn(가양역);
        when(stationService.findById(증미역_ID)).thenReturn(증미역);
        when(lineRepository.findById(구호선_ID)).thenReturn(Optional.of(구호선));

        SectionRequest sectionRequest = new SectionRequest(가양역_ID, 증미역_ID, 10);

        // when
        // lineService.addSection 호출
        lineService.addSection(구호선_ID, sectionRequest);

        // then
        // line.findLineById 메서드를 통해 검증
        LineResponse response = lineService.findById(구호선_ID);
        List<StationResponse> stations = response.getStations();
        assertThat(stations.size()).isEqualTo(2);
        assertThat(response.getStations().get(0).getName()).isEqualTo("가양역");
    }
}
