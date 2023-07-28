package nextstep.subway.unit;

import nextstep.subway.applicaion.LineSectionService;
import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.exception.ErrorType;
import nextstep.subway.exception.SectionAddException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static nextstep.subway.unit.StationFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LineSectionServiceMockTest {
    @Mock
    private LineService lineService;
    @Mock
    private StationService stationService;
    @InjectMocks
    private LineSectionService lineSectionService;

    Line 신분당선;

    @BeforeEach
    void setUp() {
        신분당선 = new Line("신분당선", "red");
        Section 논현_양재_구간 = new Section(신분당선, 논현역, 양재역, 10);
        신분당선.getSections().add(논현_양재_구간);
    }

    @Test
    void addSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        Section 양재_양재시민의숲_구간 = new Section(신분당선, 양재역, 양재시민의숲역, 10);

        when(stationService.findById(2L)).thenReturn(양재역);
        when(stationService.findById(3L)).thenReturn(양재시민의숲역);
        when(lineService.findById(1L)).thenReturn(신분당선);

        // when
        // lineService.addSection 호출
        lineSectionService.addSection(1L, new SectionRequest(2L, 3L, 10));

        // then
        // lineService.findLineById 메서드를 통해 검증
        assertThat(lineService.findById(1L).getSections()).contains(양재_양재시민의숲_구간);
    }

    @Test
    void addSectionException_withoutStations() {
        // given
        when(stationService.findById(stationIds.get(신사역))).thenReturn(신사역);
        when(stationService.findById(stationIds.get(강남역))).thenReturn(강남역);
        when(lineService.findById(1L)).thenReturn(신분당선);

        // when - then
        assertThatThrownBy(() -> {
            SectionRequest request = new SectionRequest(stationIds.get(신사역), stationIds.get(강남역), 10);
            lineSectionService.addSection(1L, request);
        }).isInstanceOf(SectionAddException.class)
                .hasMessage(ErrorType.STATIONS_NOT_EXIST_IN_LINE.getMessage());
    }

    @Test
    void addSectionException_hasAllStations() {
        // given
        when(stationService.findById(stationIds.get(논현역))).thenReturn(논현역);
        when(stationService.findById(stationIds.get(양재역))).thenReturn(양재역);
        when(lineService.findById(1L)).thenReturn(신분당선);

        // when - then
        assertThatThrownBy(() -> {
            SectionRequest request = new SectionRequest(stationIds.get(논현역), stationIds.get(양재역), 10);
            lineSectionService.addSection(1L, request);
        }).isInstanceOf(SectionAddException.class)
                .hasMessage(ErrorType.STATIONS_EXIST_IN_LINE.getMessage());
    }

    @Test
    void addSectionException_tooLongDistance() {
        // given
        when(stationService.findById(stationIds.get(논현역))).thenReturn(논현역);
        when(stationService.findById(stationIds.get(강남역))).thenReturn(강남역);
        when(lineService.findById(1L)).thenReturn(신분당선);

        // when - then
        assertThatThrownBy(() -> {
            SectionRequest request = new SectionRequest(stationIds.get(논현역), stationIds.get(강남역), 10);
            lineSectionService.addSection(1L, request);
        }).isInstanceOf(SectionAddException.class)
                .hasMessage(ErrorType.SECTION_DISTANCE_TOO_LONG.getMessage());
    }
}
