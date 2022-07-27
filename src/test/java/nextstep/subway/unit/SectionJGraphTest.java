package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static nextstep.subway.unit.LineTestUtil.DEFAULT_DISTANCE;
import static nextstep.subway.unit.LineTestUtil.개봉역;
import static nextstep.subway.unit.LineTestUtil.구일역;
import static nextstep.subway.unit.LineTestUtil.라인색;
import static nextstep.subway.unit.LineTestUtil.일호선ID;
import static nextstep.subway.unit.LineTestUtil.일호선이름;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class SectionJGraphTest {

    static Station 교대역 = new Station(3L, "교대역");
    static Station 강남역 = new Station(4L, "강남역");
    static Station 양재역 = new Station(5L, "양재역");
    static Station 남부터미널역 = new Station(6L, "남부터미널역");

    Line 이호선 = new Line(2L, "이호선", "red"  );
    Line 삼호선 = new Line(3L, "삼호선", "yellow"  );
    Line 신분당선 = new Line(4L, "신분당선", "white"  );


    @BeforeEach
    void setUp(){
        이호선.addSection(1L, 교대역, 강남역, 10);
        신분당선.addSection(2L,강남역, 양재역,10);
        삼호선.addSection(3L, 교대역, 남부터미널역, 2);
        삼호선.addSection(4L, 남부터미널역, 양재역, 3);
    }

    @Test
    @DisplayName("구간을 생성한다.")
    void addSection() {
        /**
         * 교대역    --- *2호선* ---   강남역
         * |                        |
         * *3호선*                   *신분당선*
         * |                        |
         * 남부터미널역  --- *3호선* ---   양재
         */
        String source = "v3";
        String target = "v1";

        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);
        graph.addVertex(교대역);
        graph.addVertex(강남역);
        graph.addVertex(양재역);
        graph.addVertex(남부터미널역);
        graph.setEdgeWeight(graph.addEdge(교대역, 강남역), 10);
        graph.setEdgeWeight(graph.addEdge(강남역, 양재역), 10);
        graph.setEdgeWeight(graph.addEdge(교대역, 남부터미널역), 2);
        graph.setEdgeWeight(graph.addEdge(남부터미널역, 양재역), 3);

        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
        List<Station> shortestPath = dijkstraShortestPath.getPath(교대역, 강남역).getVertexList();

        shortestPath.forEach(
                station -> System.out.println(station.toString())
        );




    }

}
