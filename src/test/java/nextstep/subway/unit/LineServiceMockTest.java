package nextstep.subway.unit;

import nextstep.subway.common.Constant;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.presentation.request.AddSectionRequest;
import nextstep.subway.line.service.LineService;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.service.SectionService;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.service.StationDto;
import nextstep.subway.station.service.StationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {

    @InjectMocks
    private LineService lineService;
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;
    @Mock
    private SectionService sectionService;

    Station 신논현역 = Station.from(Constant.신논현역);
    Station 강남역 = Station.from(Constant.강남역);
    Section 신논현역_강남역_구간 = Section.of(Station.from(Constant.신논현역), Station.from(Constant.강남역), Constant.기본_역_간격);
    Line 신분당선 = Line.of(Constant.신분당선, Constant.빨간색);

    @DisplayName("노선에 구간을 추가한다.")
    @Test
    void addSection() {
        // given
        when(stationService.findById(1L)).thenReturn(신논현역);
        when(stationService.findById(2L)).thenReturn(강남역);
        when(sectionService.save(신논현역_강남역_구간)).thenReturn(신논현역_강남역_구간);
        when(lineRepository.findById(1L)).thenReturn(Optional.of(신분당선));

        // when
        lineService.addSection(1L, AddSectionRequest.of(1L, 2L, Constant.기본_역_간격));

        // then
        assertThat(lineService.findLine(1L).getStations())
                .hasSize(2);
        assertThat(lineService.findLine(1L).getStations())
                .contains(
                        StationDto.from(신논현역),
                        StationDto.from(강남역)
                );
    }

}
