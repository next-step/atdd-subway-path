package nextstep.subway.unit;

import nextstep.subway.line.api.LineService;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.repository.LineRepository;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.Station;
import nextstep.subway.station.StationResponse;
import nextstep.subway.station.StationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;
    @InjectMocks
    private LineService lineService;

    @Test
    void addSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        Station firstStation = new Station("firstStation");
        Station secondStation = new Station("secondStation");

        given(stationService.findStation(anyLong())).willReturn(
                new StationResponse(firstStation.getId(), firstStation.getName())
        );
        given(stationService.findStation(anyLong())).willReturn(
                new StationResponse(secondStation.getId(), secondStation.getName())
        );
        given(lineRepository.findById(any(Long.class))).willReturn(Optional.of(new Line("firstLine", "blue")));

        // when
        // lineService.addSection 호출
        Line line = lineService.findLineById(1L);
        StationResponse firstStationResponse = stationService.findStation(1L);
        StationResponse secondStationResponse = stationService.findStation(2L);
        Section section = new Section(firstStationResponse.getId(), secondStationResponse.getId(), 10);
        lineService.addSection(line, section);

        // then
        // lineService.findLineById 메서드를 통해 검증
        Line savedLine = lineService.findLineById(1L);
        assertThat(savedLine.getSections().getSections().size()).isEqualTo(1);
        assertThat(savedLine.getSections().getStations()).containsAnyOf(firstStation.getId(), secondStation.getId());
    }
}
