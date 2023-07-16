package nextstep.subway.unit;

import nextstep.subway.applicaion.PathFinder;
import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PathServiceMockTest {

    @Mock
    private PathFinder pathFinder;

    @Test
    void findPath() {
        //given
        String 출발역명 = "출발역";
        Long sourceId = 1L;
        String 도착역명 = "도착역";
        Long targetId = 2L;
        when(pathFinder.find(sourceId, targetId)).thenReturn(new PathResponse(List.of(new Station(출발역명), new Station(도착역명)), 10f));
        PathService pathService = new PathService(pathFinder);

        //when
        PathResponse response = pathService.find(sourceId, targetId);

        //then
        List<String> stationNames = response.getStations().stream().map(StationResponse::getName).collect(Collectors.toList());
        assertThat(stationNames).startsWith(출발역명).endsWith(도착역명);
        assertThat(response.getDistance()).isEqualTo(10f);
    }
}
