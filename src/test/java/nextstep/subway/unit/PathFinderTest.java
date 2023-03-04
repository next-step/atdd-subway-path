package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Path;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.PathIllegalStationException;
import nextstep.subway.exception.PathNotConnectedException;
import nextstep.subway.exception.PathNotSameStationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class PathFinderTest {

    private Station 교대역;
    private Station 강남역;
    private Station 양재역;
    private Station 남부터미널역;
    private Line 이호선;
    private Line 신분당선;
    private Line 삼호선;

    /**
     * 교대역    --- *2호선* ---   강남역
     * |                        |
     * *3호선*                   *신분당선*
     * |                        |
     * 남부터미널역  --- *3호선* ---   양재
     */
    @BeforeEach
    public void setUp() {

        교대역 = new Station("교대역");
        강남역 = new Station("강남역");
        양재역 = new Station("양재역");
        남부터미널역 = new Station("남부터미널역");

        이호선 = new Line("2호선", "green", 교대역, 강남역, 10);
        신분당선 = new Line("신분당선", "red", 강남역, 양재역, 10);
        삼호선 = new Line("3호선", "orange", 교대역, 남부터미널역, 2);
        삼호선.addSection(남부터미널역, 양재역, 3);
    }

    @DisplayName("두 역의 최단 거리와 경로를 찾을 수 있다.")
    @Test
    void findPath() {
        List<Line> lines = List.of(이호선, 신분당선, 삼호선);
        PathFinder pathFinder = new PathFinder(lines);
        Path path = pathFinder.findPath(교대역, 양재역);
        assertThat(path.getStations()).containsExactly(교대역, 남부터미널역, 양재역);
        assertThat(path.getDistance()).isEqualTo(5);
    }

    @DisplayName("출발역과 도착역이 같은 경우 예외 발생")
    @Test
    void findPathSameStation() {
        List<Line> lines = List.of(이호선, 신분당선, 삼호선);
        PathFinder pathFinder = new PathFinder(lines);
        assertThatThrownBy(() ->
                pathFinder.findPath(교대역, 교대역)
        ).isInstanceOf(PathNotSameStationException.class);

    }

    @DisplayName("출발역과 도착역이 연결이 되어 있지 않은 경우 예외 발생")
    @Test
    void findPathNotConnectedStation() {
        Station 명동역 = new Station("명동역");
        Station 종각역 = new Station("종각역");

        Line 사호선 = new Line("4호선", "blue", 명동역, 종각역, 10);
        List<Line> lines = List.of(이호선, 신분당선, 삼호선, 사호선);

        PathFinder pathFinder = new PathFinder(lines);
        assertThatThrownBy(() ->
                pathFinder.findPath(교대역, 명동역)
        ).isInstanceOf(PathNotConnectedException.class);
    }

    @DisplayName("존재하지 않는 출발역 조회시 예외 발생")
    @Test
    void findPathNotFoundedStation() {
        Station 명동역 = new Station("명동역");
        List<Line> lines = List.of(이호선, 신분당선, 삼호선);

        PathFinder pathFinder = new PathFinder(lines);
        assertThatThrownBy(() ->
                pathFinder.findPath(교대역, 명동역)
        ).isInstanceOf(PathIllegalStationException.class);
    }

}
