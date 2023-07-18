package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PathFinderTest {
    private Station 강남역;
    private Station 양재역;
    private Station 교대역;
    private Station 남부터미널역;
    private Station 독바위역;
    private Station 불광역;
    private Line 삼호선;
    private Line 이호선;
    private Line 신분당선;
    private Line 육호선;

    private PathFinder pathFinder;

    /**
     * 교대역    --- *2호선* ---   강남역
     * |           10             |
     * | 2                        | 10
     * *3호선*                   *신분당선*
     * |                 3        |                         5
     * 남부터미널역  --- *3호선* ---   양재          불광역 --- *6호선* --- 독바위역
     */
    @BeforeEach
    void setUp() {
        강남역 = new Station("강남역");
        양재역 = new Station("양재역");
        교대역 = new Station("교대역");
        남부터미널역 = new Station("남부터미널역");
        독바위역 = new Station("독바위역");
        불광역 = new Station("불광역");

        삼호선 = new Line("삼호선", "orange");
        이호선 = new Line("이호선", "green");
        신분당선 = new Line("신분당선", "red");
        육호선 = new Line("육호선", "brown");

        삼호선.addSection(new Section(삼호선, 교대역, 남부터미널역, 2));
        삼호선.addSection(new Section(삼호선, 남부터미널역, 양재역, 3));
        이호선.addSection(new Section(이호선, 교대역, 강남역, 10));
        신분당선.addSection(new Section(신분당선, 강남역, 양재역, 10));
        육호선.addSection(new Section(육호선, 독바위역, 불광역, 5));

        pathFinder = new PathFinder();
    }

    @DisplayName("지하철 경로를 조회한다")
    @Test
    void findPath() {
        // given
        List<Line> lines = List.of(삼호선, 이호선, 신분당선);

        // when
        List<Station> shortestPath = pathFinder.findPath(lines, 교대역, 양재역);

        // then
        assertThat(shortestPath.stream().map(Station::getName))
                .containsExactly("교대역", "남부터미널역", "양재역");
    }

    @DisplayName("지하철 경로의 거리를 조회한다")
    @Test
    void findPathWeight() {
        // given
        List<Line> lines = List.of(삼호선, 이호선, 신분당선);

        // when
        double shortestPathWeight = pathFinder.findPathWeight(lines, 교대역, 양재역);

        // then
        assertThat(shortestPathWeight).isEqualTo(5);
    }

    @DisplayName("지하철 경로의 거리를 조회할 때 출발역과 도착역이 이어져 있지 않은 경우 에러가 발생한다")
    @Test
    void findPath_notConnect_Exception() {
        // given
        List<Line> lines = List.of(삼호선, 이호선, 신분당선, 육호선);

        // when, then
        assertThatThrownBy(() -> pathFinder.findPath(lines, 교대역, 독바위역))
                .isInstanceOf(DataIntegrityViolationException.class)
                .message()
                .isEqualTo("출발역과 도착역이 연결되어 있지 않습니다.");
    }
}