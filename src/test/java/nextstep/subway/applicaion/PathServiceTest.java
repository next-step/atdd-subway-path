package nextstep.subway.applicaion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class PathServiceTest {

    private PathService pathService;

    private StationRepository stationRepository;
    private LineRepository lineRepository;

    @BeforeEach
    void setUp() {
        stationRepository = mock(StationRepository.class);
        lineRepository = mock(LineRepository.class);
        pathService = new PathService(stationRepository, lineRepository);
    }

    @DisplayName("역간 최단경로 조회")
    @Test
    void findShortestPath() {
        var 이호선 = new Line("2호선", "green");
        var 신분당선 = new Line("신분당선", "red");
        var 삼호선 = new Line("3호선", "orange");

        var 교대역 = createStationStep(1L, "교대역");
        var 양재역 = createStationStep(2L, "양재역");
        var 강남역 = createStationStep(3L, "강남역");
        var 남부터미널역 = createStationStep(4L, "남부터미널역");

        이호선.addSection(교대역, 강남역, 10);
        신분당선.addSection(강남역, 양재역, 10);
        삼호선.addSection(교대역, 남부터미널역, 2);
        삼호선.addSection(남부터미널역, 양재역, 3);

        when(lineRepository.findAll())
                .thenReturn(List.of(이호선, 신분당선, 삼호선));

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