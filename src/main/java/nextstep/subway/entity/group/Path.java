package nextstep.subway.entity.group;

import java.util.List;
import java.util.Objects;
import nextstep.subway.entity.Section;
import nextstep.subway.entity.Station;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

public class Path {

    private final WeightedMultigraph<Station, DefaultWeightedEdge> graph;
    private final GraphPath<Station, DefaultWeightedEdge> graphPath;

    public Path(List<Section> sections, Station source, Station target) {

        validateDontEquals(source, target);

        graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        graphPath = getGraph(sections, source, target);
    }

    private void validateDontEquals(Station source, Station target) {
        if (source == target) {
            throw new IllegalArgumentException(("출발지와 목적지가 같은 역일 수 없습니다."));
        }
    }

    private GraphPath<Station, DefaultWeightedEdge> getGraph(List<Section> sections, Station source, Station target) {

        sections.forEach(section -> {
            Station upStation = section.getUpStation();
            Station downStation = section.getDownStation();

            graph.addVertex(upStation);
            graph.addVertex(downStation);
            graph.setEdgeWeight(graph.addEdge(upStation, downStation), section.getDistance());
        });

        GraphPath<Station, DefaultWeightedEdge> path = new DijkstraShortestPath(graph).getPath(source, target);

        if (Objects.isNull(path)) {
            throw new IllegalArgumentException("출발지와 목적지의 각 구간이 이어져있지 않습니다.");
        }

        return path;
    }

    public List<Station> getPath() {

        return graphPath.getVertexList();
    }

    public int getPathDistance() {

        return (int) graphPath.getWeight();
    }
}
