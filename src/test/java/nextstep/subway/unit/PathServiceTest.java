package nextstep.subway.unit;

import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.StationNotFoundException;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.domain.path.DepartureDestinationCannotReachableException;
import nextstep.subway.domain.path.DepartureDestinationCannotSameException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("경로 조회 단위 테스트")
@Transactional
@SpringBootTest
public class PathServiceTest {

    @Autowired
    private PathService pathService;

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private LineRepository lineRepository;

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
        교대역 = stationRepository.save(new Station("교대역"));
        강남역 = stationRepository.save(new Station("강남역"));
        양재역 = stationRepository.save(new Station("양재역"));
        남부터미널역 = stationRepository.save(new Station("남부터미널역"));

        이호선 = lineRepository.save(new Line("2호선", "green"));
        이호선.addSection(new Section(이호선, 교대역, 강남역, 10));

        신분당선 = lineRepository.save(new Line("신분당선", "red"));
        신분당선.addSection(new Section(신분당선, 강남역, 양재역, 10));

        삼호선 = lineRepository.save(new Line("3호선", "orange"));
        삼호선.addSection(new Section(신분당선, 교대역, 남부터미널역, 2));
        삼호선.addSection(new Section(신분당선, 남부터미널역, 양재역, 3));
    }

    @DisplayName("출발역과 도착역이 같은경우 예외발생")
    @Test
    void exception_when_departure_station_and_destination_station_are_same() {
        // When & Then
        assertThatThrownBy(() -> pathService.findShortestPath(강남역.getId(), 강남역.getId()))
                .isInstanceOf(DepartureDestinationCannotSameException.class);
    }

    @DisplayName("출발역과 도착역이 연결이 되어 있지 않은 경우 예외발생")
    @Test
    void exception_when_departure_station_and_destination_station_are_not_reachable() {
        // Given
        Station 부산역 = stationRepository.save(new Station("부산역"));
        Station 강원역 = stationRepository.save(new Station("강원역"));

        Line 구호선 = lineRepository.save(new Line("9호선", "gold"));

        구호선.addSection(new Section(구호선, 부산역, 강원역, 10));

        // When & Then
        assertThatThrownBy(() -> pathService.findShortestPath(강남역.getId(), 부산역.getId()))
                .isInstanceOf(DepartureDestinationCannotReachableException.class);
     }

    @DisplayName("존재하지 않은 출발역이나 도착역을 조회 할 경우 예외발생")
    @Test
    void exception_when_not_exist_station() {
        // Given
        Station 부산역 = stationRepository.save(new Station("부산역"));

        // When & Then
        assertThatThrownBy(() -> pathService.findShortestPath(강남역.getId(), 부산역.getId()))
                .isInstanceOf(StationNotFoundException.class);
    }

    @DisplayName("출발역과 도착역 사이의 최단 경로를 조회한다.")
    @Test
    void findShortestPath() {
        // When
        PathResponse response = pathService.findShortestPath(교대역.getId(), 양재역.getId());

        // Then
        assertThat(getStations(response)).containsExactly(교대역.getName(), 남부터미널역.getName(), 양재역.getName());
        assertThat(response.getDistance()).isEqualTo(5);
    }

    @DisplayName("출발역과 도착역 사이의 최단 경로를 조회한다.")
    @Test
    void findShortestPathDifferentLine() {
        // When
        PathResponse response = pathService.findShortestPath(강남역.getId(), 남부터미널역.getId());

        // Then
        assertThat(getStations(response)).containsExactly(강남역.getName(), 교대역.getName(), 남부터미널역.getName());
        assertThat(response.getDistance()).isEqualTo(12);
    }

    private List<String> getStations(PathResponse response) {
        return response.getStations().stream()
                .map(StationResponse::getName)
                .collect(Collectors.toList());
    }
}
