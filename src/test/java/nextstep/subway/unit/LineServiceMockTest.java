package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;

    @Mock
    private StationService stationService;

    @Mock
    private StationRepository stationRepository;

    private LineService lineService;

    @BeforeEach
    void setUp() {
        lineService = new LineService(lineRepository, stationService);
    }

    @DisplayName("구간 생성")
    @Test
    void addSection() {
        Long lineId = 1L;
        Line 신분당선 = Line.of("신분당선", "bg-red-600");
        Station 논현역 = Station.of("논현역");
        Station 신논현역 = Station.of("신논현역");
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        when(stationService.findById(1L)).thenReturn(논현역);
        when(stationService.findById(2L)).thenReturn(신논현역);
        when(lineRepository.findById(lineId)).thenReturn(Optional.of(신분당선));

        // when
        // lineService.addSection 호출
        SectionRequest sectionRequest = new SectionRequest(1L, 2L, 5);
        lineService.addSection(lineId, sectionRequest);

        // then
        // line.findLineById 메서드를 통해 검증
        신분당선 = lineRepository.findById(lineId).get();
        assertThat(신분당선.sections().size()).isEqualTo(1);
        assertThat(신분당선.stations()).contains(논현역);
        assertThat(신분당선.stations()).contains(신논현역);
    }
}
