package nextstep.subway.path.service;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.repository.LineRepository;
import nextstep.subway.path.domain.LineSectionEdge;
import nextstep.subway.path.payload.SearchPathRequest;
import nextstep.subway.path.payload.ShortestPathResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.exception.NonExistentStationException;
import nextstep.subway.station.payload.StationResponse;
import nextstep.subway.station.repository.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PathQueryServiceTest {

    private StationRepository stationRepository;

    private LineRepository lineRepository;

    private PathQueryService pathQueryService;

    private ShortestPathFinder<LineSectionEdge, Long> shortestPathFinder;


    private Station 교대역;
    private Station 강남역;
    private Station 양재역;
    private Station 남부터미널역;

    private List<Station> 전체역;

    private Line 이호선;
    private Line 신분당선;
    private Line 삼호선;


    /**
     * 교대역    --- *2호선* (10) ---    강남역
     * |                                 |
     * *3호선* (2)                   *신분당선* (10)
     * |                                 |
     * 남부터미널역  --- *3호선* (3) ---   양재
     */

    @BeforeEach
    void setUp() {
        교대역 = new Station(1L, "교대역");
        강남역 = new Station(2L, "강남역");
        양재역 = new Station(3L, "양재역");
        남부터미널역 = new Station(4L, "남부터미널역");
        전체역 = List.of(교대역, 강남역, 양재역, 남부터미널역);

        이호선 = new Line("2호선", "green", new Section(교대역.getId(), 강남역.getId(), 10L));
        신분당선 = new Line("신분당선", "red", new Section(강남역.getId(), 양재역.getId(), 10L));
        삼호선 = new Line("3호선", "orange", new Section(교대역.getId(), 남부터미널역.getId(), 2L));
        삼호선.addSection(남부터미널역.getId(), 양재역.getId(), 3L);

        shortestPathFinder = new ShortestPathFinder<>();
        stationRepository = mock(StationRepository.class);
        lineRepository = mock(LineRepository.class);
        pathQueryService = new PathQueryService(stationRepository, lineRepository, shortestPathFinder);

    }


    @DisplayName("역이 존재하지 않으면 에러를 반환한다")
    @Test
    void whenStationNonExistThenThrow() {
        Long 없는역 = -1L;
        SearchPathRequest request = new SearchPathRequest(교대역.getId(), 없는역);
        when(stationRepository.findByIdIn(List.of(request.getSource(), request.getTarget())))
                .thenReturn(List.of(교대역));

        assertThrows(NonExistentStationException.class,
                () -> pathQueryService.findShortestPath(request));
    }

    @Test
    @DisplayName("최단 거리와 경로를 반환한다")
    void whenShowShortestPath() {
        SearchPathRequest request = new SearchPathRequest(교대역.getId(), 양재역.getId());

        when(stationRepository.findByIdIn(anyCollection()))
                .thenAnswer(invocation -> {
                    Collection<Long> ids = invocation.getArgument(0);
                    return ids.stream()
                            .map(this::findStationById)
                            .collect(Collectors.toList());
                });

        when(lineRepository.findAll())
                .thenReturn(List.of(이호선, 신분당선, 삼호선));

        ShortestPathResponse shortestPath = pathQueryService.findShortestPath(request);
        assertAll(
                () -> assertThat(shortestPath.getDistance()).isEqualTo(5L),
                () -> assertThat(getPathNames(shortestPath))
                        .containsExactly(교대역.getName(), 남부터미널역.getName(), 양재역.getName())

        );
    }

    private List<String> getPathNames(final ShortestPathResponse shortestPath) {
        return shortestPath.getStations().stream()
                .map(StationResponse::getName)
                .collect(Collectors.toList());
    }

    private Station findStationById(final Long id) {
        return 전체역.stream().filter(it -> it.getId().equals(id))
                .findFirst()
                .orElseThrow();
    }
}
