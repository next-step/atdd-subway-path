package nextstep.subway.unit.path.infrastructure;

import static org.assertj.core.api.Assertions.*;

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import nextstep.subway.line.domain.Distance;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Sections;
import nextstep.subway.path.infrastructure.JgraphtPathFinder;
import nextstep.subway.path.infrastructure.PathFinder;
import nextstep.subway.path.infrastructure.dto.StationPaths;
import nextstep.subway.station.domain.Station;

@DisplayName("경로 찾기 테스트")
public class PathFinderTest {
    private Station 교대역;
    private Station 강남역;
    private Station 양재역;
    private Station 남부터미널역;

    private Sections allSections;

    /**
     * Given 지하철 노선과 노선에 포함되는 지하철 역이 있을때
     *
     * 교대역     --- *2호선* --- 강남역
     * ㅣ                          ㅣ
     * *3호선*                  *신분당선*
     * ㅣ                          ㅣ
     * 남부터미널역 --- *3호선* --- 양재역
     *
     * 교대역 - 강남역 - 양재역 거리 = 20
     * 교대역 - 남부터미널역 - 양재역 거리 = 12
     */
    @BeforeEach
    public void setUp() {
        교대역 = new Station(1L, "교대역");
        강남역 = new Station(2L, "강남역");
        양재역 = new Station(3L, "양재역");
        남부터미널역 = new Station(4L, "남부터미널역");

        Line 이호선 = new Line(1L, "이호선", "green");
        Line 신분당선 = new Line(2L, "신분당선", "blue");
        Line 삼호선 = new Line(3L, "삼호선", "red");

        Distance distance10 = new Distance(10);
        Distance distance2 = new Distance(2);
        이호선.addSection(교대역, 강남역, distance10);
        신분당선.addSection(강남역, 양재역, distance10);
        삼호선.addSection(교대역, 남부터미널역, distance2);
        삼호선.addSection(남부터미널역, 양재역, distance10);

        allSections = Stream.of(이호선, 신분당선, 삼호선)
                            .map(Line::getSections)
                            .reduce(Sections::union)
                            .get();
    }

    private static Stream<PathFinder> pathFinders() {
        return Stream.of(
            new JgraphtPathFinder()
        );
    }

    @DisplayName("최단 경로 찾기")
    @MethodSource("pathFinders")
    @ParameterizedTest
    void findShortestPaths(PathFinder pathFinder) {
        StationPaths path = allSections.shortestPaths(pathFinder, 교대역, 양재역);

        assertThat(path.getStations()).containsExactly(교대역, 남부터미널역, 양재역);
        assertThat(path.getDistance().getValue()).isEqualTo(12);
    }

    @DisplayName("최단 경로 찾기 실패 - 출발역과 도착역이 같은 경우")
    @MethodSource("pathFinders")
    @ParameterizedTest
    void findShortestPathsFailCase1(PathFinder pathFinder) {
        assertThatThrownBy(() -> allSections.shortestPaths(pathFinder, 교대역, 교대역))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("최단 경로 찾기 실패 - 출발역과 도착역이 연결되어 있지 않은 경우")
    @MethodSource("pathFinders")
    @ParameterizedTest
    void findShortestPathsFailCase2(PathFinder pathFinder) {
        Line 사호선 = new Line(3L, "삼호선", "red");
        Station 동작역 = new Station(5L, "동작역");
        Station 이촌역 = new Station(6L, "이촌역");
        사호선.addSection(동작역, 이촌역, new Distance(100));

        assertThatThrownBy(() -> allSections.shortestPaths(pathFinder, 교대역, 이촌역))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("최단 경로 찾기 실패 - 존재하지 않는 출발역이나 도착역을 조회할 경우")
    @MethodSource("pathFinders")
    @ParameterizedTest
    void findShortestPathsFailCase3(PathFinder pathFinder) {
        Station 유령역 = new Station("유령역");

        assertThatThrownBy(() -> allSections.shortestPaths(pathFinder, 교대역, 유령역))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
