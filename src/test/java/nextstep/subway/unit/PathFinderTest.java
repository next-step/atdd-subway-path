package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Arrays;
import java.util.List;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PathFinderTest {

    private Station 교대역;
    private Station 강남역;
    private Station 양재역;
    private Station 남부터미널역;
    private Line 이호선;
    private Line 신분당선;
    private Line 삼호선;

    @BeforeEach
    void setUp() {
        교대역 = new Station("교대역");
        강남역 = new Station("강남역");
        양재역 = new Station("양재역");
        남부터미널역 = new Station("남부터미널역");

        이호선 = new Line("이호선", "rainbow");
        이호선.addSection(교대역, 강남역, 10);

        신분당선 = new Line("신분당선", "rainbow");
        신분당선.addSection(강남역, 양재역, 10);

        삼호선 = new Line("삼호선", "rainbow");
        삼호선.addSection(교대역, 남부터미널역, 2);
        삼호선.addSection(남부터미널역, 양재역, 3);
    }

    @Test
    void findPath() {
        List<Line> lines = Arrays.asList(이호선, 신분당선, 삼호선);
        PathFinder pathFinder = new PathFinder(lines, 교대역, 양재역);

        List<Station> path = pathFinder.getPath();
        int distance = pathFinder.getDistance();

        assertThat(path).containsExactly(교대역, 남부터미널역, 양재역);
        assertThat(distance).isEqualTo(5);
    }

    @Test
    void findPath_SourceIsNotLinkedWithTarget() {
        Station 서울숲역 = new Station("서울숲역");
        Station 왕십리역 = new Station("왕십리역");
        Line 수인분당선 = new Line("수인분당선", "rainbow");
        수인분당선.addSection(서울숲역, 왕십리역, 10);

        List<Line> lines = Arrays.asList(이호선, 신분당선, 삼호선, 수인분당선);
        assertThatThrownBy(() -> new PathFinder(lines, 교대역, 서울숲역))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void findPath_SourceIsEqualToTarget() {
        List<Line> lines = Arrays.asList(이호선, 신분당선, 삼호선);
        assertThatThrownBy(() -> new PathFinder(lines, 교대역, 교대역))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
