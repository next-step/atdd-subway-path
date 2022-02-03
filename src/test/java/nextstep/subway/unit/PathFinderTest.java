package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import nextstep.subway.domain.line.Line;
import nextstep.subway.domain.path.PathFinder;
import nextstep.subway.domain.path.PathFinderUsingWeightedMultigraph;
import nextstep.subway.domain.path.ShortestPath;
import nextstep.subway.domain.section.Section;
import nextstep.subway.domain.station.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PathFinderTest {

    private Line line1;
    private Line line2;
    private Line line3;
    private Line line4;
    private Station 일번역;
    private Station 이번역;
    private Station 삼번역;
    private Station 사번역;
    private int distanceFromStation1to2 = 1;
    private int distanceFromStation2to3 = 50;
    private int distanceFromStation3to4 = 5;
    private int distanceFromStation4to1 = 7;
    /**
     * 1번역      ---      *1호선*    ---       2번역
     * |                 distance: 1           |
     * *4호선*                                *2호선*
     * | distance: 7                       distance: 3
     * |                                        |
     * 4번역      ---      *3호선*    ---       3번역
     *                    distance: 5
     */

    @BeforeEach
    void setUp() {
        일번역 = Station.of("일번역");
        이번역 = Station.of("이번역");
        삼번역 = Station.of("삼번역");
        사번역 = Station.of("사번역");

        line1 = Line.of("1호선", "파란색", Section.of(일번역, 이번역, distanceFromStation1to2));
        line2 = Line.of("2호선", "빨간색", Section.of(이번역, 삼번역, distanceFromStation2to3));
        line3 = Line.of("3호선", "노란색", Section.of(삼번역, 사번역, distanceFromStation3to4));
        line4 = Line.of("4호선", "보라색", Section.of(사번역, 일번역, distanceFromStation4to1));
    }

    @Test
    void kShortest_를_이용한_최단거리역_적용_테스트() {

        // When
        PathFinder pathFinder = new PathFinderUsingWeightedMultigraph(
            Arrays.asList(line1, line2, line3, line4));
        List<ShortestPath> shortestPaths = pathFinder.executeKShortest(일번역, 삼번역);

        // Then
        for (ShortestPath shortestPath : shortestPaths) {
            assertThat(shortestPath.stations()).startsWith(일번역);
            assertThat(shortestPath.stations()).endsWith(삼번역);
        }

    }

    @Test
    void dijkstraShortest_를_이용한_최단거리역_적용_테스트() {

        // When
        PathFinder pathFinder = new PathFinderUsingWeightedMultigraph(Arrays.asList(line1, line2, line3, line4));
        ShortestPath shortestPath = pathFinder.executeDijkstra(일번역, 삼번역);

        // Then
        assertThat(shortestPath.stations()).containsExactly(일번역, 사번역, 삼번역);

        // And
        assertThat(shortestPath.totalDistance()).isEqualTo(distanceFromStation4to1 + distanceFromStation3to4);
    }

}
