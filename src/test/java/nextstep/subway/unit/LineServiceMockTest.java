package nextstep.subway.unit;

import nextstep.subway.line.entity.Line;
import nextstep.subway.line.service.LineDataService;
import nextstep.subway.section.SectionCreateRequest;
import nextstep.subway.section.SectionService;
import nextstep.subway.station.Station;
import nextstep.subway.station.service.StationDataService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private LineDataService lineDataService;
    @Mock
    private StationDataService stationDataService;
    @InjectMocks
    private SectionService sectionService;

    @DisplayName("노선에 구간을 등록한다.")
    @Test
    void addSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        Long 이호선id = 1L;
        Long 잠실역id = 1L;
        Long 성수역id = 2L;

        Line 이호선 = new Line("이호선", "green");

        Station 잠실역 = new Station("잠실역");
        Station 성수역 = new Station("성수역");

        when(lineDataService.findLine(이호선id)).thenReturn(이호선);
        when(stationDataService.findStation(잠실역id)).thenReturn(잠실역);
        when(stationDataService.findStation(성수역id)).thenReturn(성수역);

        SectionCreateRequest request = new SectionCreateRequest(잠실역id, 성수역id, 10);

        // when
        // lineService.addSection 호출
        sectionService.saveSection(1L, request);

        // then
        // lineService.findLineById 메서드를 통해 검증
        assertThat(이호선.getSections().getSections()).hasSize(1);
    }
}
