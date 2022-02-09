package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;
    @Autowired
    private LineService lineService;

    private Station 강남역;
    private Station 양재역;
    private Station 정자역;
    private Line 신분당선;

    @BeforeEach
    void setUp() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        lineService = new LineService(lineRepository, stationService);
        강남역 = new Station("강남역");
        ReflectionTestUtils.setField(강남역, "id", 1L);
        양재역 = new Station("양재역");
        ReflectionTestUtils.setField(양재역, "id", 2L);
        정자역 = new Station("정자역");
        ReflectionTestUtils.setField(정자역, "id", 3L);
        신분당선 = new Line("신분당선", "bg-red-60");
        신분당선.addSection(new Section(신분당선, 강남역, 양재역, 10));
        ReflectionTestUtils.setField(신분당선, "id", 1L);
    }

    @Test
    void addSection() {
        when(lineRepository.findById(신분당선.getId())).thenReturn(Optional.of(신분당선));
        when(stationService.findById(양재역.getId())).thenReturn(양재역);
        when(stationService.findById(정자역.getId())).thenReturn(정자역);
        // when
        // lineService.addSection 호출
        lineService.addSection(신분당선.getId(), new SectionRequest(양재역.getId(), 정자역.getId(), 10));
        // then
        // line.findLineById 메서드를 통해 검증
        Line line = lineService.findById(1L);
        assertThat(line.allSections().size()).isEqualTo(2);
    }
}
