package nextstep.subway.applicaion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import nextstep.subway.applicaion.path.PathFinder;
import nextstep.subway.applicaion.path.exception.IllegalSourceTargetException;
import nextstep.subway.applicaion.path.exception.PathNotFoundException;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class PathFinderTest {

    @Autowired
    private LineRepository lineRepository;
    @Autowired
    private StationRepository stationRepository;

    private Station 교대역;
    private Station 강남역;
    private Station 양재역;
    private Station 양재시민의숲역;
    private Station 남부터미널역;
    private List<Section> sectionList;

    @BeforeEach
    void setUp() {
        var 이호선 = createLine("2호선", "green");
        var 삼호선 = createLine("3호선", "orange");
        var 신분당선 = createLine("신분당선", "red");

        교대역 = createStation("교대역");
        강남역 = createStation("강남역");
        양재역 = createStation("양재역");
        양재시민의숲역 = createStation("양재시민의숲역");
        남부터미널역 = createStation("남부터미널역");

        sectionList = new ArrayList<>();
        sectionList.add(new Section(이호선, 교대역, 강남역, 10));
        sectionList.add(new Section(신분당선, 강남역, 양재역, 10));
        sectionList.add(new Section(신분당선, 양재역, 양재시민의숲역, 10));
        sectionList.add(new Section(삼호선, 교대역, 남부터미널역, 2));
        sectionList.add(new Section(삼호선, 남부터미널역, 양재역, 3));
    }

    @DisplayName("역간 최단경로 탐색")
    @Test
    void findShortestPath() {
        var pathFinder = new PathFinder(sectionList);

        var path = pathFinder.solve(교대역, 양재역);

        assertAll(
                () -> assertThat(path.getDistance()).isEqualTo(5),
                () -> assertThat(path.getStations()).containsExactly(교대역, 남부터미널역, 양재역)
        );
    }

    @DisplayName("역간 최단경로 탐색 (환승구간 존재)")
    @Test
    void findShortestPathWithTransfer() {
        var pathFinder = new PathFinder(sectionList);

        var path = pathFinder.solve(교대역, 양재시민의숲역);

        assertAll(
                () -> assertThat(path.getDistance()).isEqualTo(15),
                () -> assertThat(path.getStations()).containsExactly(교대역, 남부터미널역, 양재역, 양재시민의숲역)
        );
    }

    @DisplayName("출발역 <-> 도착역 교체시 최단경로 동일")
    @Test
    void reversedShortestPath() {
        var pathFinder = new PathFinder(sectionList);

        var forwardPath = pathFinder.solve(교대역, 양재시민의숲역);
        var reversedPath = pathFinder.solve(양재시민의숲역, 교대역);

        var reversedStations = new ArrayList<>(reversedPath.getStations());
        Collections.reverse(reversedStations);
        assertAll(
                () -> assertThat(forwardPath.getDistance()).isEqualTo(reversedPath.getDistance()),
                () -> assertThat(forwardPath.getStations()).containsExactlyElementsOf(reversedStations)
        );
    }

    @DisplayName("연결되지 않은 역에 대하여 경로탐색 실패")
    @Test
    void findPathFailsWhenStationsAreNotConnected() {
        var 일호선 = createLine("일호선", "blue");
        var 신도림 = createStation("신도림");
        var 구로 = createStation("구로");

        sectionList.add(new Section(일호선, 신도림, 구로, 10));
        var pathFinder = new PathFinder(sectionList);

        assertThrows(PathNotFoundException.class, () -> pathFinder.solve(교대역, 신도림));
    }

    @DisplayName("동일 역에 대하여 경로탐색 실패")
    @Test
    void findPathFailsForSameStations() {
        var pathFinder = new PathFinder(sectionList);
        assertThrows(IllegalSourceTargetException.class, () -> pathFinder.solve(교대역, 교대역));
    }

    private Line createLine(String name, String color) {
        var line = new Line(name, color);
        return lineRepository.save(line);
    }

    private Station createStation(String name) {
        return stationRepository.save(new Station(name));
    }
}