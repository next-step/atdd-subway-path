package nextstep.subway.unit;


import nextstep.subway.application.LineService;
import nextstep.subway.application.PathService;
import nextstep.subway.application.StationService;
import nextstep.subway.application.dto.PathResponse;
import nextstep.subway.application.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PathServiceMockTest {
    private Station 교대역;
    private Station 강남역;
    private Station 양재역;
    private Station 남부터미널역;
    private Line 이호선;
    private Line 신분당선;
    private Line 삼호선;
    @Mock
    private LineService lineService;
    @Mock
    private PathFinder pathFinder;
    @Mock
    private StationService stationService;

    @BeforeEach
    public void setUp() {
        교대역 = new Station(1L, "교대역");
        강남역 = new Station(2L, "강남역");
        양재역 = new Station(3L, "양재역");
        남부터미널역 = new Station(4L, "남부터미널역");

        이호선 = new Line(1L, "2호선", "green", 교대역, 강남역, 10);
        신분당선 = new Line(2L, "신분당선", "red", 강남역, 양재역, 10);
        삼호선 = new Line(3L, "3호선", "orange", 교대역, 남부터미널역, 2);
        삼호선.addSection(남부터미널역, 양재역, 3);
    }
    @DisplayName("최단거리 경로를 조회한다.")
    @Test
    void findPath() {
        // Given
        final Long source = 교대역.getId();
        final Long target = 양재역.getId();
        final List<Line> lines = Arrays.asList(이호선, 신분당선, 삼호선);
        when(lineService.findAllLine()).thenReturn(lines);
        when(pathFinder.findPath(lines, 교대역, 양재역))
                .thenReturn(new PathResponse(Arrays.asList(교대역, 남부터미널역, 양재역), 5));
        when(stationService.findStationById(source)).thenReturn(교대역);
        when(stationService.findStationById(target)).thenReturn(양재역);

        final PathService pathService = new PathService(lineService, pathFinder, stationService);

        // When
        final PathResponse pathResponse = pathService.findPath(source, target);

        // Then
        final List<StationResponse> stations = pathResponse.getStations();
        assertThat(stations.get(0).getName()).isEqualTo("교대역");
        assertThat(stations.get(1).getName()).isEqualTo("남부터미널역");
        assertThat(stations.get(2).getName()).isEqualTo("양재역");
        assertThat(pathResponse.getDistance()).isEqualTo(5);
    }
}
