package nextstep.subway.unit;

import nextstep.subway.applicaion.PathFinder;
import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PathServiceMockTest {

    @Mock
    private PathFinder pathFinder;

    @Test
    void findPath() {
        //given
        Long sourceId = 1L;
        Long targetId = 2L;
        when(pathFinder.find(sourceId, targetId))
                .thenReturn(List.of(new StationResponse(sourceId, "출발역"), new StationResponse(targetId, "도착역")));
        PathService pathService = new PathService(pathFinder);

        //when
        PathResponse response = pathService.find(sourceId, targetId);

        //then
        List<Long> stationIds = response.getStations().stream().map(StationResponse::getId).collect(Collectors.toList());
        Assertions.assertThat(stationIds).startsWith(sourceId).endsWith(targetId);
    }
}
