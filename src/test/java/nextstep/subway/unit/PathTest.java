package nextstep.subway.unit;

import nextstep.subway.applicaion.dto.SectionRequest;
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
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class PathTest {

    private Station 교대역;
    private Station 강남역;
    private Station 양재역;
    private Station 남부터미널역;

    private Line 이호선;
    private Line 신분당선;
    private Line 삼호선;

    private WeightedMultigraph<String, DefaultWeightedEdge> graph;

    @BeforeEach
    public void setUp() {
        graph = new WeightedMultigraph(DefaultWeightedEdge.class);

        교대역 = 지하철역_생성_요청(1L, "교대역");
        강남역 = 지하철역_생성_요청(2L, "강남역");
        양재역 = 지하철역_생성_요청(3L,"양재역");
        남부터미널역 = 지하철역_생성_요청(4L,"남부터미널역");

        이호선 = 지하철_노선_생성_요청(1L, "2호선", "green", 교대역, 강남역, 1);
        신분당선 = 지하철_노선_생성_요청(2L, "신분당선", "red", 강남역, 양재역, 2);
        삼호선 = 지하철_노선_생성_요청(3L, "3호선", "orange", 교대역, 남부터미널역, 3);

        지하철_노선에_지하철_구간_생성_요청(삼호선, 남부터미널역, 양재역, 10);
    }

    @DisplayName("출발역-도착역 사이 최단경로 조회")
    @Test
    void findShortestPath() {
        String source = 교대역.getName();
        String target = 강남역.getName();

        // given
        graph.addVertex(교대역.getName());
        graph.addVertex(남부터미널역.getName());
        graph.addVertex(강남역.getName());
        graph.addVertex(양재역.getName());
        graph.setEdgeWeight(graph.addEdge(교대역.getName(), 남부터미널역.getName()), 3);
        graph.setEdgeWeight(graph.addEdge(강남역.getName(), 양재역.getName()), 2);
        graph.setEdgeWeight(graph.addEdge(교대역.getName(), 강남역.getName()), 3);
        graph.setEdgeWeight(graph.addEdge(남부터미널역.getName(), 양재역.getName()), 10);

        // when
        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
        List<String> shortestPath = dijkstraShortestPath.getPath(source, target).getVertexList();

        // then
        assertThat(shortestPath.size()).isEqualTo(3);
    }

    public Station 지하철역_생성_요청(Long id, String name) {
        return new Station(id, name);
    }

    public Line 지하철_노선_생성_요청(Long id, String name, String color, Station upStation, Station downStation, int distance) {
        Line line = new Line(id, name, color);
        line.addSection(new Section(line, upStation, downStation, distance));

        return line;
    }

    public Section 지하철_노선에_지하철_구간_생성_요청(Line line, Station upStation, Station downStation, int distance) {
        Section section = new Section(line, upStation, downStation, distance);
        line.addSection(section);

        return section;
    }

}