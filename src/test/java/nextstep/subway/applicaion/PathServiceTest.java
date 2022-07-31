package nextstep.subway.applicaion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class PathServiceTest {

    private PathService pathService;

    private StationRepository stationRepository;
    private PathFinder pathFinder;

    @BeforeEach
    void setUp() {
        stationRepository = mock(StationRepository.class);
        pathFinder = mock(PathFinder.class);
        pathService = new PathService(stationRepository, pathFinder);
    }

    @DisplayName("역간 최단경로 조회")
    @Test
    void findShortestPath() {
        var 교대역 = createStationStep(1L, "교대역");
        var 양재역 = createStationStep(2L, "양재역");
        var 남부터미널역 = createStationStep(3L, "남부터미널역");

        when(pathFinder.solve(교대역, 양재역))
                .thenReturn(new Path(List.of(교대역, 양재역, 남부터미널역), 5));

        var response = pathService.findShortestPath(교대역.getId(), 양재역.getId());

        assertAll(
                () -> assertThat(response.getDistance()).isEqualTo(5),
                () -> assertThat(response.getStations()).contains(
                        new StationResponse(교대역.getId(), "교대역"),
                        new StationResponse(남부터미널역.getId(), "남부터미널역"),
                        new StationResponse(양재역.getId(), "양재역")
                )
        );
    }

    private Station createStationStep(Long id, String name) {
        var station = new Station(name);
        ReflectionTestUtils.setField(station, "id", id);

        when(stationRepository.findById(id)).thenReturn(Optional.of(station));

        return station;
    }
}