package atdd.station.service;

import atdd.station.domain.SubwayLine;
import atdd.station.domain.SubwayLineRepository;
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

import static atdd.station.fixture.SubwayLineFixture.getFirstSubwayLine;
import static atdd.station.fixture.SubwayLineFixture.getSecondSubwayLine;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SoftAssertionsExtension.class)
public class PathServiceTest {
    @Mock
    SubwayLineRepository subwayLineRepository;

    @InjectMocks
    PathService pathService;

    @DisplayName("지하철역_사이의_최단거리_꼉로_조회가_되는지")
    @Test
    public void findPathTest(SoftAssertions softly) {
        //given
        List<SubwayLine> subwayLines = Arrays.asList(getSecondSubwayLine(), getFirstSubwayLine());

        //when
        when(subwayLineRepository.findAll()).thenReturn(subwayLines);
        PathFindResponseDto shortestPath = pathService.findPath(0L, 3L);

        //then
        softly.assertThat(shortestPath.getStartStationId()).isEqualTo(0L);
        softly.assertThat(shortestPath.getEndStationId()).isEqualTo(0L);
        softly.assertThat(shortestPath.getStations().size()).isEqualTo(3);
    }
}
