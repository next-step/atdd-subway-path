package nextstep.subway.infrastructure;

import nextstep.subway.applicaion.PathFinder;
import nextstep.subway.applicaion.dto.PathResult;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class JgraphtShortestPathFinder implements PathFinder {

    public PathResult findPath(List<Line> lines, Station srcStation, Station dstStation) {
        List<Section> distinctSections = lines.stream()
                .flatMap(line -> line.getSections().stream())
                .distinct()
                .collect(Collectors.toList());
        List<Station> distinctStations = distinctSections.stream()
                .flatMap(section -> Stream.of(section.getUpStation(), section.getDownStation()))
                .distinct()
                .collect(Collectors.toList());

        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        for (Station distinctStation : distinctStations) {
            graph.addVertex(distinctStation);
        }
        for (Section distinctSection : distinctSections) {
            graph.setEdgeWeight(graph.addEdge(distinctSection.getUpStation(), distinctSection.getDownStation()), distinctSection.getDistance());
        }

        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
        GraphPath path = dijkstraShortestPath.getPath(srcStation, dstStation);
        List<Station> vertexList = path.getVertexList();
        long weight = (int) path.getWeight();
        return new PathResult(vertexList, weight);
    }
}
