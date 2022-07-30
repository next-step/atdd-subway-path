package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class PathFinderTest {
    Station 교대역;
    Station 강남역;
    Station 양재역;
    Station 남부터미널역;

    Line 이호선;
    Line 삼호선;
    Line 신분당선;

    List<Section> sectionList;

    @BeforeEach
    void setUp() {
        교대역 = new Station(1L, "교대역");
        강남역 = new Station(2L, "강남역");
        양재역 = new Station(3L, "양재역");
        남부터미널역 = new Station(4L, "남부터미널역");

        이호선 = new Line("2호선", "green");
        삼호선 = new Line("3호선", "orange");
        신분당선 = new Line("신분당선", "red");

        이호선.addSection(교대역, 강남역, 10);
        삼호선.addSection(교대역, 남부터미널역, 10);
        신분당선.addSection(교대역, 남부터미널역, 10);
        삼호선.addSection(남부터미널역, 양재역, 3);

        List<Line> lineList = Arrays.asList(이호선, 삼호선, 신분당선);
        sectionList = lineList.stream()
                .flatMap(line -> line.getSectionList()
                        .stream())
                .collect(Collectors.toList());
    }

    @Test
    @DisplayName("최단경로 조회")
    void getShortestPaths() {
        //when
        PathFinder pathFinder = new PathFinder(sectionList);
        List<Station> stationList = pathFinder.getShortestPath(교대역, 양재역);
        //then
        assertThat(stationList).hasSize(3)
                .containsExactly(교대역, 남부터미널역, 양재역);
        assertThat(pathFinder.getShortestDistance(교대역, 양재역))
                .isEqualTo(10 + 3);
    }

}