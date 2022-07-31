package nextstep.subway.applicaion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class PathFinderTest {

    private PathFinder pathFinder;

    @Autowired
    private LineRepository lineRepository;
    @Autowired
    private StationRepository stationRepository;

    private Station 교대역;
    private Station 강남역;
    private Station 양재역;
    private Station 남부터미널역;

    @BeforeEach
    void setUp() {
        pathFinder = new PathFinder(lineRepository, stationRepository);

        var 이호선 = createLine("2호선", "green");
        var 삼호선 = createLine("3호선", "orange");
        var 신분당선 = createLine("신분당선", "red");

        교대역 = createStation("교대역");
        강남역 = createStation("강남역");
        양재역 = createStation("양재역");
        남부터미널역 = createStation("남부터미널역");

        이호선.addSection(교대역, 강남역, 10);
        신분당선.addSection(강남역, 양재역, 10);
        삼호선.addSection(교대역, 남부터미널역, 2);
        삼호선.addSection(남부터미널역, 양재역, 3);
    }

    @DisplayName("역간 최단경로 탐색")
    @Test
    void findShortestPath() {
        var path = pathFinder.solve(교대역, 양재역);

        assertAll(
                () -> assertThat(path.getDistance()).isEqualTo(5),
                () -> assertThat(path.getStations()).containsExactly(교대역, 남부터미널역, 양재역)
        );
    }

    @DisplayName("연결되지 않은 역에 대하여 경로탐색 실패")
    @Test
    void findPathFailsWhenStationsAreNotConnected() {
        var 일호선 = createLine("일호선", "blue");
        var 신도림 = createStation("신도림");
        var 구로 = createStation("구로");
        일호선.addSection(신도림, 구로, 10);

        assertThrows(IllegalStateException.class, () -> pathFinder.solve(교대역, 신도림));
    }

    @DisplayName("동일 역에 대하여 경로탐색 실패")
    @Test
    void findPathFailsForSameStations() {
        assertThrows(IllegalArgumentException.class, () -> pathFinder.solve(교대역, 교대역));
    }

    private Line createLine(String name, String color) {
        var line = new Line(name, color);
        return lineRepository.save(line);
    }

    private Station createStation(String name) {
        return stationRepository.save(new Station(name));
    }
}