package nextstep.subway.unit;

import nextstep.subway.domain.*;
import org.jgrapht.GraphPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("다익스트라 알고리즘 지하철 경로 조회")
public class StationDijkstraShortestPathFinderTest {

    private final static Station 교대역 = new Station(1L, "교대역");
    private final static Station 강남역 = new Station(2L, "강남역");
    private final static Station 양재역 = new Station(3L, "양재역");
    private final static Station 남부터미널역 = new Station(4L, "남부터미널역");

    private StationDijkstraShortestStationPathFinder pathFinder;

    @BeforeEach
    void setUp() {
        List<Line> lines = new ArrayList<>();
        Line 이호선 = new Line("2호선", "bg-green-600");
        Line 삼호선 = new Line("3호선", "bg-orange-600");
        Line 신분당선 = new Line("신분당선", "bg-pink-600");


        이호선.addSection(new Section(이호선, 교대역, 강남역, 10));
        신분당선.addSection(new Section(신분당선, 강남역, 양재역, 10));
        삼호선.addSection(new Section(삼호선, 교대역, 남부터미널역, 2));
        삼호선.addSection(new Section(삼호선, 남부터미널역, 양재역, 3));

        lines.add(이호선);
        lines.add(삼호선);
        lines.add(신분당선);

        pathFinder = StationDijkstraShortestStationPathFinder.ofLines(new Lines(lines));
    }

    @Test
    @DisplayName("지하철 경로 조회")
    void getPaths() {
        GraphPath path = pathFinder.getPath(교대역.getId().toString(), 양재역.getId().toString());

        assertThat(path.getVertexList()).hasSize(3);
        assertThat(path.getWeight()).isEqualTo(5);
    }

}
