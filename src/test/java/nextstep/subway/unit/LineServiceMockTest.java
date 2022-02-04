package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
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
    private Station 정자역;
    private Station 판교역;

    private Line 신분당선;

    @BeforeEach
    void setUp() {
        강남역 = Station.of("강남");
        양재역 = Station.of("양재");
        미금역 = Station.of("미금");
        정자역 = Station.of("정자");
        판교역 = Station.of("판교");
        신분당선 = new Line("신분당선", "red", 강남역, 미금역, 100);
        ReflectionTestUtils.setField(강남역, "id", 1L);
        ReflectionTestUtils.setField(양재역, "id", 2L);
        ReflectionTestUtils.setField(미금역, "id", 3L);
        ReflectionTestUtils.setField(정자역, "id", 4L);
        ReflectionTestUtils.setField(판교역, "id", 5L);
        ReflectionTestUtils.setField(신분당선, "id", 1L);
    }

    @Test
    void addSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        LineRepository lineRepository = mock(LineRepository.class);
        StationService stationService = mock(StationService.class);
        LineService lineService = new LineService(lineRepository, stationService);
        Section section = new Section(신분당선, 정자역, 판교역, 50);
        when(stationService.findById(4L)).thenReturn((정자역));
        when(stationService.findById(5L)).thenReturn((판교역));
        when(lineRepository.findById(신분당선.getId())).thenReturn(Optional.ofNullable(신분당선));
        // when
        // lineService.addSection 호출
        lineService.addSection(1L, new SectionRequest(section));

        // then
        // line.findLineById 메서드를 통해 검증
        LineResponse line = lineService.findById(1L);
        assertThat(line.getStations().get(3).getId()).isEqualTo(정자역.getId());
        assertThat(line.getStations().get(2).getId()).isEqualTo(판교역.getId());

    }
}
