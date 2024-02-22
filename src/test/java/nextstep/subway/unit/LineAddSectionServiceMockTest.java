package nextstep.subway.unit;

import nextstep.subway.line.Line;
import nextstep.subway.line.LineRepository;
import nextstep.subway.line.addsection.LineAddSectionRequest;
import nextstep.subway.line.addsection.LineAddSectionService;
import nextstep.subway.line.addsection.LineAddedSectionResponse;
import nextstep.subway.station.Station;
import nextstep.subway.station.StationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class LineAddSectionServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationRepository stationRepository;
    @InjectMocks
    private LineAddSectionService lineAddSectionService;

    @Test
    void addSection() {
        // given
        Mockito.when(stationRepository.findById(2L)).thenReturn(Optional.of(new Station(2L, "양재역")));
        Mockito.when(stationRepository.findById(3L)).thenReturn(Optional.of(new Station(3L, "판교역")));
        Mockito.when(lineRepository.findById(any())).thenReturn(Optional.of(new Line("신분당선", "RED", new Station(1L, "강남역"), new Station(2L, "양재역"), 10L)));


        LineAddSectionRequest request = new LineAddSectionRequest(2L, 3L, 10L);

        // when
        LineAddedSectionResponse response = lineAddSectionService.addSection(1L, request);

        // then
        Mockito.verify(lineRepository).findById(any());
        Mockito.verify(stationRepository).findById(2L);
        Mockito.verify(stationRepository).findById(3L);
        assertThat(response.getStations()).hasSize(3);
    }
}
