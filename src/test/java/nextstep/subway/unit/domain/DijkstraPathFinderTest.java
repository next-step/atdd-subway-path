package nextstep.subway.unit.domain;

import nextstep.subway.applicaion.StationNotFoundException;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.path.DepartureDestinationCannotReachableException;
import nextstep.subway.domain.path.DepartureDestinationCannotSameException;
import nextstep.subway.domain.path.PathFinder;
import nextstep.subway.domain.path.SubwayMap;
import nextstep.subway.domain.path.Path;
import nextstep.subway.domain.path.DijkstraPathFinder;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("경로 찾기 관련")
@Transactional
public class DijkstraPathFinderTest {
    private PathFinder pathFinder;

    private Station 교대역;
    private Station 강남역;
    private Station 양재역;
    private Station 남부터미널역;

    private Line 이호선;
    private Line 신분당선;
    private Line 삼호선;

    /**
     * 교대역 --- 거리: 10, *2호선* --- 강남역
     * |                              |
     * 거리: 2, *3호선*               거리: 10, *신분당선*
     * |                                 |
     * 남부터미널역 --- 거리: 3, *3호선* --- 양재역
     */
    @BeforeEach
    public void setUp() {
        교대역 = new Station("교대역");
        강남역 = new Station("강남역");
        양재역 = new Station("양재역");
        남부터미널역 = new Station("남부터미널역");

        이호선 = new Line("2호선", "green");
        이호선.addSection(new Section(이호선, 교대역, 강남역, 10));

        신분당선 = new Line("신분당선", "red");
        신분당선.addSection(new Section(신분당선, 강남역, 양재역, 10));

        삼호선 = new Line("3호선", "orange");
        삼호선.addSection(new Section(신분당선, 교대역, 남부터미널역, 2));
        삼호선.addSection(new Section(신분당선, 남부터미널역, 양재역, 3));
    }

    @DisplayName("출발역과 도착역의 최단거리를 조회한다")
    @Test
    void findPath() {
        // Given
        List<Line> lines = List.of(이호선, 신분당선, 삼호선);
        SubwayMap subwayMap = new SubwayMap(lines, new WeightedMultigraph<>(DefaultWeightedEdge.class));

        pathFinder = new DijkstraPathFinder(subwayMap);

        // When
        Path path = pathFinder.findPath(강남역, 남부터미널역);

        // Then
        assertThat(path.getStations()).containsExactly(강남역, 교대역, 남부터미널역);
        assertThat(path.getDistance().getValue()).isEqualTo(12);
    }

    @DisplayName("출발역과 도착역이 같은경우 예외발생")
    @Test
    void exception_when_departure_station_and_destination_station_are_same() {
        // Given
        List<Line> lines = List.of(이호선, 신분당선, 삼호선);
        SubwayMap subwayMap = new SubwayMap(lines, new WeightedMultigraph<>(DefaultWeightedEdge.class));

        pathFinder = new DijkstraPathFinder(subwayMap);

        // When & Then
        assertThatThrownBy(() -> pathFinder.findPath(강남역, 강남역))
                .isInstanceOf(DepartureDestinationCannotSameException.class);
    }

    @DisplayName("출발역과 도착역이 연결이 되어 있지 않은 경우 예외발생")
    @Test
    void exception_when_departure_station_and_destination_station_are_not_reachable() {
        // Given
        Station 부산역 = new Station("부산역");
        Station 강원역 = new Station("강원역");
        Line 구호선 = new Line("9호선", "gold");
        구호선.addSection(new Section(구호선, 부산역, 강원역, 10));

        List<Line> lines = List.of(이호선, 신분당선, 삼호선, 구호선);
        SubwayMap subwayMap = new SubwayMap(lines, new WeightedMultigraph<>(DefaultWeightedEdge.class));

        pathFinder = new DijkstraPathFinder(subwayMap);

        // When & Then
        assertThatThrownBy(() -> pathFinder.findPath(강남역, 부산역))
                .isInstanceOf(DepartureDestinationCannotReachableException.class);
    }

    @DisplayName("존재하지 않은 출발역이나 도착역을 조회 할 경우 예외발생")
    @Test
    void exception_when_not_exist_station() {
        // Given
        Station 부산역 = new Station("부산역");

        List<Line> lines = List.of(이호선, 신분당선, 삼호선);
        SubwayMap subwayMap = new SubwayMap(lines, new WeightedMultigraph<>(DefaultWeightedEdge.class));

        pathFinder = new DijkstraPathFinder(subwayMap);

        // When & Then
        assertThatThrownBy(() -> pathFinder.findPath(강남역, 부산역))
                .isInstanceOf(StationNotFoundException.class);
    }
}