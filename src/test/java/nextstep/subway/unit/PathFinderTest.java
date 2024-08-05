package nextstep.subway.unit;

import nextstep.subway.domain.*;
import nextstep.subway.exception.PathNotFoundException;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PathFinderTest {

    private static final Long 강남역_ID = 1L;
    private static final Long 신논현역_ID = 2L;
    private static final Long 신사역_ID = 3L;
    private static final Long 판교역_ID = 4L;
    private static final Long 정자역_ID = 5L;

    private Station 강남역;
    private Station 신논현역;
    private Station 신사역;
    private Station 판교역;
    private Station 정자역;

    private PathFinder pathFinder;
    private WeightedMultigraph<Station, DefaultWeightedEdge> graph;

    @BeforeEach
    void setUp() {
        graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);

        강남역 = new Station("강남역");
        신논현역 = new Station("신논현역");
        신사역 = new Station("신사역");
        판교역 = new Station("판교역");
        정자역 = new Station("정자역");

        ReflectionTestUtils.setField(강남역, "id", 강남역_ID);
        ReflectionTestUtils.setField(신논현역, "id", 신논현역_ID);
        ReflectionTestUtils.setField(신사역, "id", 신사역_ID);
        ReflectionTestUtils.setField(판교역, "id", 판교역_ID);
        ReflectionTestUtils.setField(정자역, "id", 정자역_ID);

        List<Station> allStations = Arrays.asList(강남역, 신논현역, 신사역, 판교역, 정자역);

        Line 신분당선 = new Line("신분당선", "bg-red-600");
        Line 다른노선 = new Line("다른노선", "bg-blue-600");

        Section section1 = Section.createSection(신분당선, 강남역, 신논현역, 10);
        Section section2 = Section.createSection(신분당선, 신논현역, 신사역, 15);
        Section section3 = Section.createSection(다른노선, 신사역, 판교역, 20);
        Section section4 = Section.createSection(다른노선, 판교역, 정자역, 25);
        Section section5 = Section.createSection(다른노선, 신논현역, 판교역, 30);

        List<Section> allSections = Arrays.asList(section1, section2, section3, section4, section5);

        pathFinder = PathFinderFactory.createPathFinder(allSections, allStations);
        graph = pathFinder.getGraph();
    }

    @Test
    @DisplayName("경로 그래프 초기화")
    void initializePathGraph() {
        assertThat(pathFinder).isNotNull();
        assertThat(graph.vertexSet()).containsExactlyInAnyOrder(강남역, 신논현역, 신사역, 판교역, 정자역);
        assertThat(graph.edgeSet()).hasSize(5);

        assertThat(graph.getEdgeWeight(graph.getEdge(강남역, 신논현역))).isEqualTo(10);
        assertThat(graph.getEdgeWeight(graph.getEdge(신논현역, 신사역))).isEqualTo(15);
        assertThat(graph.getEdgeWeight(graph.getEdge(신사역, 판교역))).isEqualTo(20);
        assertThat(graph.getEdgeWeight(graph.getEdge(신논현역, 판교역))).isEqualTo(30);
        assertThat(graph.getEdgeWeight(graph.getEdge(판교역, 정자역))).isEqualTo(25);

        assertThat(graph.containsEdge(강남역, 신논현역)).isTrue();
        assertThat(graph.containsEdge(신논현역, 신사역)).isTrue();
        assertThat(graph.containsEdge(신사역, 판교역)).isTrue();
        assertThat(graph.containsEdge(판교역, 정자역)).isTrue();
        assertThat(graph.containsEdge(신논현역, 판교역)).isTrue();
    }

    @Test
    @DisplayName("최소 길이 경로 찾기")
    void getShortestPath() {
        Station 강남역 = graph.vertexSet().stream().filter(s -> s.getId().equals(강남역_ID)).findFirst().orElseThrow();
        Station 정자역 = graph.vertexSet().stream().filter(s -> s.getId().equals(정자역_ID)).findFirst().orElseThrow();

        PathResult shortestPath = pathFinder.getShortestPath(강남역, 정자역);

        assertThat(shortestPath).isNotNull();
        assertThat(shortestPath.getPathStations()).extracting(Station::getId)
                .containsExactly(강남역_ID, 신논현역_ID, 판교역_ID, 정자역_ID);
    }

    @Test
    @DisplayName("연결되지 않은 경로를 찾을 때 예외 발생")
    void getShortestPathNoRoute() {
        Station 고립역 = new Station("고립역");
        Long 고립역_ID = 6L;
        ReflectionTestUtils.setField(고립역, "id", 고립역_ID);
        graph.addVertex(고립역);

        Station 강남역 = graph.vertexSet().stream().filter(s -> s.getId().equals(강남역_ID)).findFirst().orElseThrow();

        PathNotFoundException exception = assertThrows(PathNotFoundException.class, () -> {
            pathFinder.getShortestPath(강남역, 고립역);
        });

        assertThat(exception.getMessage()).contains(강남역_ID.toString(), 고립역_ID.toString());
    }

    @Test
    @DisplayName("출발역과 도착역이 동일할 때 예외 발생")
    void getShortestPathSameStations() {
        Station 강남역 = graph.vertexSet().stream().filter(s -> s.getId().equals(강남역_ID)).findFirst().orElseThrow();

        PathNotFoundException exception = assertThrows(PathNotFoundException.class, () -> {
            pathFinder.getShortestPath(강남역, 강남역);
        });

        assertThat(exception.getMessage())
                .contains(String.format("출발역과 도착역(ID: %d)이 동일하여 경로를 찾을 수 없습니다.", 강남역_ID));
    }

}
