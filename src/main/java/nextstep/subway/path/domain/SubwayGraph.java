package nextstep.subway.path.domain;

import nextstep.subway.line.domain.Section;
import nextstep.subway.station.domain.Station;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;

public class SubwayGraph {

    private final WeightedMultigraph<Station, DefaultWeightedEdge> graph;

    public SubwayGraph(List<Station> stations, List<Section> sections) {
        this.graph = new WeightedMultigraph(DefaultWeightedEdge.class);

        stations.forEach(this::addVertex);
        sections.forEach(this::setEdgeWeight);
    }

    private void addVertex(Station station) {
        graph.addVertex(station);
    }

    private void setEdgeWeight(Section section) {
        graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance());
    }

    public Path getShortestPath(Station source, Station target) {
        validateSourceTargetNotSame(source, target);

        GraphPath graphPath;

        try {
            DijkstraShortestPath path = new DijkstraShortestPath(graph);
            graphPath = path.getPath(source, target);

            if (graphPath == null) {
                throw new RuntimeException("출발역과 도착역이 연결이 되어 있지 않은 경우 실패");
            }
        } catch (IllegalArgumentException exception) {
            throw new RuntimeException("존재하지 않은 출발역이나 도착역을 조회 할 경우 실패");
        }

        return new Path(graphPath.getVertexList(), (int) graphPath.getWeight());
    }

    private void validateSourceTargetNotSame(Station source, Station target) {
        if (source == target) {
            throw new RuntimeException("시작역, 끝역 동일하면 실패");
        }
    }
}
