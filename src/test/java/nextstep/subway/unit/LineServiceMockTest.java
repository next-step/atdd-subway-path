package nextstep.subway.unit;

import nextstep.subway.line.Line;
import nextstep.subway.line.LineRepository;
import nextstep.subway.line.LineResponse;
import nextstep.subway.line.LineService;
import nextstep.subway.line.section.*;
import nextstep.subway.station.Station;
import nextstep.subway.station.StationRepository;
import nextstep.subway.station.StationResponse;
import nextstep.subway.station.StationService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.OngoingStubbing;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;

    @Mock
    private SectionRepository sectionRepository;

    @Mock
    private StationRepository stationRepository;

    @Mock
    private StationService stationService;

    @Test
    @DisplayName("지하철 노선에 구간을 추가한다.")
    void addSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        Line lineMock = new Line("2호선", "green");
        lineMock.setId(1L);
        when(lineRepository.findById(1L)).thenReturn(Optional.of(lineMock));

        when(stationService.findStationById(1L)).thenReturn(new Station("강남역"));
        when(stationService.findStationById(2L)).thenReturn(new Station("역삼역"));

        // when
        // lineService.addSection 호출
        SectionService sectionService = new SectionService(stationService, lineRepository, sectionRepository, stationRepository);
        sectionService.addSection(lineMock.getId(), new SectionRequest(1L, 2L, 10));

        // then
        // lineService.findLineById 메서드를 통해 검증
        LineService lineService = new LineService(lineRepository, stationRepository);
        Line line = lineService.findLineById(lineMock.getId());
        assertThat(line.getSections()).hasSize(1);
    }
}
