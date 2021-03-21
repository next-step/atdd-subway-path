package nextstep.subway.path.application;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Section;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PathServiceMockTest {

    @Mock
    private LineService lineService;

    @Mock
    private StationService stationService;

    private PathService pathService;

    private Line 신분당선;
    private Line 이호선;
    private Line 삼호선;

    private Station 강남역;
    private Station 양재역;
    private Station 교대역;
    private Station 남부터미널역;

    @BeforeEach
    void setUp() {
        pathService = new PathService(lineService, stationService);

        강남역 = createStation(1L, "강남역");
        양재역 = createStation(2L, "양재역");
        교대역 = createStation(3L, "교대역");
        남부터미널역 = createStation(4L, "남부터미널역");

        신분당선 = createLine(1L, "신분당선", "bg-red-400", 강남역, 양재역, 10);
        이호선 = createLine(2L, "이호선", "bg-red-500", 교대역, 강남역, 10);
        삼호선 = createLine(3L, "삼호선", "bg-red-600", 교대역, 양재역, 5);

        삼호선.addSection(new Section(삼호선, 교대역, 남부터미널역, 3));
    }

    private Station createStation(Long id, String name) {
        Station station = new Station(name);
        ReflectionTestUtils.setField(station, "id", id);

        return station;
    }

    private Line createLine(Long id, String name, String color, Station upStation, Station downStation, int distance) {
        Line line = new Line(name, color, upStation, downStation, distance);
        ReflectionTestUtils.setField(line, "id", id);

        return line;
    }

    @DisplayName("최단거리 조회")
    @Test
    void findShortestPath() {
        // given
        when(stationService.findById(교대역.getId())).thenReturn(교대역);
        when(stationService.findById(양재역.getId())).thenReturn(양재역);
        when(lineService.findLines()).thenReturn(Arrays.asList(신분당선, 이호선, 삼호선));

        // when
        PathResponse response = pathService.findShortestPath(교대역.getId(), 양재역.getId());

        List<Long> stationIds = response.getStations().stream()
                .map(it -> it.getId())
                .collect(Collectors.toList());

        List<Long> expectedStationIds = Stream.of(교대역, 남부터미널역, 양재역)
                .map(it -> it.getId())
                .collect(Collectors.toList());

        assertThat(stationIds).containsExactlyElementsOf(expectedStationIds);
    }

    @DisplayName("최단거리 조회_1")
    @Test
    void findShortestPath_1() {
        // given
        when(stationService.findById(강남역.getId())).thenReturn(강남역);
        when(stationService.findById(남부터미널역.getId())).thenReturn(남부터미널역);
        when(lineService.findLines()).thenReturn(Arrays.asList(신분당선, 이호선, 삼호선));

        // when
        PathResponse response = pathService.findShortestPath(강남역.getId(), 남부터미널역.getId());

        List<Long> stationIds = response.getStations().stream()
                .map(it -> it.getId())
                .collect(Collectors.toList());

        List<Long> expectedStationIds = Stream.of(강남역, 양재역, 남부터미널역)
                .map(it -> it.getId())
                .collect(Collectors.toList());

        assertThat(stationIds).containsExactlyElementsOf(expectedStationIds);
    }

    @DisplayName("최단거리 조회 withGraphService")
    @Test
    void findShortestPathWithGraphService() {
        // given
        when(stationService.findById(교대역.getId())).thenReturn(교대역);
        when(stationService.findById(양재역.getId())).thenReturn(양재역);
        when(lineService.findLines()).thenReturn(Arrays.asList(신분당선, 이호선, 삼호선));

        // when
        PathResponse response = pathService.findShortestPathWithGraphService(교대역.getId(), 양재역.getId());

        List<Long> stationIds = response.getStations().stream()
                .map(it -> it.getId())
                .collect(Collectors.toList());

        List<Long> expectedStationIds = Stream.of(교대역, 남부터미널역, 양재역)
                .map(it -> it.getId())
                .collect(Collectors.toList());

        assertThat(stationIds).containsExactlyElementsOf(expectedStationIds);
    }

    @DisplayName("최단거리 조회_1 withGraphService")
    @Test
    void findShortestPathWithGraphService_1() {
        // given
        when(stationService.findById(강남역.getId())).thenReturn(강남역);
        when(stationService.findById(남부터미널역.getId())).thenReturn(남부터미널역);
        when(lineService.findLines()).thenReturn(Arrays.asList(신분당선, 이호선, 삼호선));

        // when
        PathResponse response = pathService.findShortestPathWithGraphService(강남역.getId(), 남부터미널역.getId());

        List<Long> stationIds = response.getStations().stream()
                .map(it -> it.getId())
                .collect(Collectors.toList());

        List<Long> expectedStationIds = Stream.of(강남역, 양재역, 남부터미널역)
                .map(it -> it.getId())
                .collect(Collectors.toList());

        assertThat(stationIds).containsExactlyElementsOf(expectedStationIds);
    }
}