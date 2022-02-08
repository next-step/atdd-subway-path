package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.unit.PathFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PathServiceTest {

    @Mock
    private LineService lineService;
    @Mock
    private StationService stationService;

    @Test
    void shortest() {
        //given
        when(stationService.findById(남터역.getId())).thenReturn(남터역);
        when(stationService.findById(양재역.getId())).thenReturn(양재역);
        when(lineService.findAllLines()).thenReturn(Arrays.asList(이호선, 삼호선, 신분당선));

        PathService pathService = new PathService(lineService, stationService);

        //when
        PathResponse shortestPath = pathService.findShortestPath(양재역.getId(), 남터역.getId());
        List<String> namesOfStations = shortestPath.getStations().stream()
                .map(StationResponse::getName)
                .collect(Collectors.toList());

        //then
        assertAll(
                () -> assertThat(shortestPath.getDistance()).isEqualTo(30),
                () -> assertThat(namesOfStations).containsExactly(양재역.getName(), 강남역.getName(), 교대역.getName(), 남터역.getName())
        );

    }

}
