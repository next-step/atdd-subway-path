package nextstep.subway.unit;

import nextstep.subway.service.LineService;
import nextstep.subway.service.PathFinder;
import nextstep.subway.service.PathService;
import nextstep.subway.service.StationService;
import nextstep.subway.service.dto.LineDto;
import nextstep.subway.service.dto.PathDto;
import nextstep.subway.service.dto.StationDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static nextstep.subway.helper.fixture.LineFixture.신분당선_엔티티;
import static nextstep.subway.helper.fixture.LineFixture.이호선_엔티티;
import static nextstep.subway.helper.fixture.StationFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PathServiceMockTest {
    @Mock
    private LineService lineService;
    @Mock
    private StationService stationService;
    @Mock
    private PathFinder pathFinder;
    @InjectMocks
    private PathService pathService;

    @Test
    @DisplayName("findShortestPath를 호출하면 path 정보를 조회할 수 있다.")
    void succeed() {
        final Long sourceStationId = 1L;
        final Long targetStationId = 2L;

        // given
        List<LineDto> givenLines = List.of(
                LineDto.from(신분당선_엔티티(강남역_엔티티, 논현역_엔티티)),
                LineDto.from(이호선_엔티티(강남역_엔티티, 역삼역_엔티티)));

        PathDto givenPathDto = new PathDto(List.of(
                StationDto.from(강남역_엔티티),
                StationDto.from(논현역_엔티티)),
                5);
        when(stationService.findStationById(sourceStationId)).thenReturn(강남역_엔티티);
        when(stationService.findStationById(targetStationId)).thenReturn(논현역_엔티티);
        when(lineService.findAllLines()).thenReturn(givenLines);
        when(pathFinder.findShortestPath(givenLines, 강남역_엔티티, 논현역_엔티티)).thenReturn(givenPathDto);

        // when
        PathDto path = pathService.findShortestPath(sourceStationId, targetStationId);

        // then
        assertThat(path.getDistance()).isEqualTo(givenPathDto.getDistance());
        assertThat(path.getStations()).containsExactlyElementsOf(givenPathDto.getStations());

        verify(stationService, times(1)).findStationById(sourceStationId);
        verify(stationService, times(1)).findStationById(targetStationId);
        verify(lineService, times(1)).findAllLines();
    }
}
