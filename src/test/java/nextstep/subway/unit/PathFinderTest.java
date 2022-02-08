package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.util.ReflectionTestUtils.setField;

class PathFinderTest {

    PathFinder pathFinder;
    Station 교대역;
    Station 강남역;
    Station 양재역;
    Station 남부터미널역;
    int totalDistance = 5;
    List<Line> lines = new ArrayList<>();

    /**
     * 교대역 --- *2호선(10)* ---   강남역
     * |                        |
     * *3호선(2)*                *신분당선(10)*
     * |                        |
     * 남부터미널역 --- *3호선(3)* --- 양재
     */
    @BeforeEach
    void setUp() {
        교대역 = new Station("교대역");
        강남역 = new Station("강남역");
        양재역 = new Station("양재역");
        남부터미널역 = new Station("남부터미널역");

        setField(교대역, "id", 1L);
        setField(강남역, "id", 2L);
        setField(양재역, "id", 3L);
        setField(남부터미널역, "id", 4L);

        Line 이호선 = new Line("2호선", "green");
        Line 신분당선 = new Line("신분당선", "red");
        Line 삼호선 = new Line("3호선", "orange");

        이호선.addSection(new Section(이호선, 교대역, 강남역, 10));
        신분당선.addSection(new Section(신분당선, 강남역, 양재역, 10));
        삼호선.addSection(new Section(삼호선, 교대역, 남부터미널역, 2));
        삼호선.addSection(new Section(삼호선, 남부터미널역, 양재역, 3));

        lines.add(이호선);
        lines.add(신분당선);
        lines.add(삼호선);

        pathFinder = new PathFinder(lines);
    }

    @Test
    @DisplayName("출발역과 도착역으로 최단 경로 내의 지하철역 목록을 반환한다")
    void getShortestPaths() {
        // when
        List<Station> shortestPath = pathFinder.getShortestPaths(교대역, 양재역);

        // then
        assertThat(shortestPath).contains(교대역, 남부터미널역, 양재역);
    }

    @Test
    void getTotalDistance() {
        // given
        List<Station> shortestPath = pathFinder.getShortestPaths(교대역, 양재역);

        // when
        int findTotalDistance = pathFinder.getPathDistance(교대역, 양재역);

        // then
        assertThat(findTotalDistance).isEqualTo(totalDistance);
    }
}