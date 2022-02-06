package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

    private Station 강남역;
    private Station 양재역;
    private Station 미금역;
    private Section 강남역에서_양재역;
    private Section 양재역에서_미금역;
    private Line 신분당선;

    @BeforeEach
    void setUp() {
        강남역 = Station.of("강남");
        양재역 = Station.of("양재");
        미금역 = Station.of("미금");
        신분당선 = new Line("신분당선", "red");
        강남역에서_양재역 = new Section(신분당선, 강남역, 양재역, 50);
        양재역에서_미금역 = new Section(신분당선, 양재역, 미금역, 50);
        신분당선.init(강남역에서_양재역);

        ReflectionTestUtils.setField(강남역, "id", 1L);
        ReflectionTestUtils.setField(양재역, "id", 2L);
        ReflectionTestUtils.setField(미금역, "id", 3L);
        ReflectionTestUtils.setField(신분당선, "id", 1L);
        ReflectionTestUtils.setField(강남역에서_양재역, "id", 1L);
        ReflectionTestUtils.setField(양재역에서_미금역, "id", 2L);
    }

    @Test
    void addSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        LineRepository lineRepository = mock(LineRepository.class);
        StationService stationService = mock(StationService.class);
        SectionRepository sectionRepository = mock(SectionRepository.class);
        LineService lineService = new LineService(lineRepository, stationService, sectionRepository);
        when(stationService.findById(2L)).thenReturn(양재역);
        when(stationService.findById(3L)).thenReturn(미금역);
        when(sectionRepository.findById(1L)).thenReturn(Optional.ofNullable(강남역에서_양재역));
        when(sectionRepository.findById(2L)).thenReturn(Optional.ofNullable(양재역에서_미금역));
        when(lineRepository.findById(신분당선.getId())).thenReturn(Optional.ofNullable(신분당선));

        // when
        // lineService.addSection 호출
        lineService.addSection(1L, new SectionRequest(양재역에서_미금역));

        // then
        // line.findLineById 메서드를 통해 검증
        LineResponse lineResponse = lineService.findById(1L);
        assertThat(lineResponse.getStations().get(1).getId()).isEqualTo(양재역.getId());
        assertThat(lineResponse.getStations().get(2).getId()).isEqualTo(미금역.getId());
    }
}
