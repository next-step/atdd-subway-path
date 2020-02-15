package atdd.station.service;

import atdd.station.domain.Station;
import atdd.station.domain.StationRepository;
import atdd.station.dto.path.PathFindResponseDto;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static atdd.station.fixture.StationFixture.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SoftAssertionsExtension.class)
public class PathServiceTest {
    @Mock
    StationRepository stationRepository;

    @InjectMocks
    StationService stationService;

    @DisplayName("지하철역_사이의_최단거리_꼉로_조회가_되는지")
    @Test
    public void findPathTest(SoftAssertions softly) {
        //given
        List<Station> stations = Arrays.asList(KANGNAM_STATON, YUCKSAM_STATON, SUNLENG_STATON);

        //when
        when(stationRepository.findAll()).thenReturn(stations);
        PathFindResponseDto shortestPath = stationService.findPath();

        //then
        softly.assertThat(shortestPath.getStartStationId()).isEqualTo(1L);
        softly.assertThat(shortestPath.getEndStationId()).isEqualTo(5L);
        softly.assertThat(shortestPath.getStations().size()).isEqualTo(4);
    }
}
