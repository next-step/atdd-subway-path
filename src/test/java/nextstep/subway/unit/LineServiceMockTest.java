package nextstep.subway.unit;

import nextstep.subway.controller.dto.LineResponse;
import nextstep.subway.controller.dto.SectionCreateRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.repository.LineRepository;
import nextstep.subway.domain.repository.SectionRepository;
import nextstep.subway.service.LineService;
import nextstep.subway.service.StationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@DisplayName("구간 서비스 단위 테스트 with Mock")
@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private SectionRepository sectionRepository;
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;
    @InjectMocks
    private LineService lineService;


    @Test
    @DisplayName("구간을 등록한다.")
    void addSection() {
        // given
        Station 신림역 = Station.builder().id(1L).name("신림역").build();
        Station 보라매역 = Station.builder().id(2L).name("보라매역").build();
        Station 보라매병원역 = Station.builder().id(3L).name("보라매병원역").build();

        Line 신림선 = Line.builder()
                .name("신림선")
                .color("BLUE")
                .build();

        Section 신림보라매구간 = Section.builder()
                .line(신림선)
                .upStation(신림역)
                .downStation(보라매역)
                .distance(10L)
                .build();
        신림선.addSection(신림보라매구간);

        Section 보라매보라매병원구간 = Section.builder()
                .line(신림선)
                .upStation(보라매역)
                .downStation(보라매병원역)
                .distance(10L)
                .build();

        when(stationService.getStationById(anyLong())).thenReturn(보라매역);
        when(stationService.getStationById(anyLong())).thenReturn(보라매병원역);
        when(lineRepository.findById(anyLong())).thenReturn(Optional.of(신림선));
        when(sectionRepository.save(any())).thenReturn(보라매보라매병원구간);


        // when
        SectionCreateRequest 구간_생성_요청 = SectionCreateRequest.builder()
                .upStationId(String.valueOf(보라매역.getId()))
                .downStationId(String.valueOf(보라매병원역.getId()))
                .distance(22L)
                .build();
        lineService.addSection(1L, 구간_생성_요청);


        // then
        LineResponse lineResponse = lineService.findLineById(1L);
        assertThat(lineResponse.getStations()).hasSize(3);
    }
}
