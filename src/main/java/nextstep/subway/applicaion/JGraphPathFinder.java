package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
public class JGraphPathFinder implements PathFinder {
    @Override
    public PathResponse find(List<Line> lines, Station source, Station target) {
        final var subwayGraph = makeSubwayGraph(lines);
        final var dijkstraShortestPath = new DijkstraShortestPath<>(subwayGraph);
        int distance = (int) dijkstraShortestPath.getPathWeight(source, target);
        List<Station> paths = dijkstraShortestPath.getPath(source, target).getVertexList();
        return PathResponse.toResponse(distance, paths);
    }

    private WeightedMultigraph<Station, DefaultWeightedEdge> makeSubwayGraph(List<Line> lines) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        lines.stream()
                .map(Line::getSections)
                .flatMap(Collection::stream)
                .forEach(section -> create(graph, section));
        return graph;
    }

    private void create(WeightedMultigraph<Station, DefaultWeightedEdge> graph, nextstep.subway.domain.Section section) {
        Station upStation = section.getUpStation();
        Station downStation = section.getDownStation();
        int distance = section.getDistance();
        graph.addVertex(upStation);
        graph.addVertex(downStation);
        graph.setEdgeWeight(graph.addEdge(upStation, downStation), distance);
    }
}
