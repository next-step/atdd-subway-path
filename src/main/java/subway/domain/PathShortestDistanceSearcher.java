package subway.domain;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PathShortestDistanceSearcher implements PathSearcher {

    private final DijkstraShortestPath<PathStation, PathSectionJGraph> path;

    public static PathShortestDistanceSearcher from(List<PathSection> sections) {
        return new PathShortestDistanceSearcher(sections);
    }

    private PathShortestDistanceSearcher(List<PathSection> sections) {
        WeightedMultigraph<PathStation, PathSectionJGraph> graph = new WeightedMultigraph<>(PathSectionJGraph.class);

        Set<PathStation> stations = getStations(sections);
        stations.forEach(graph::addVertex);

        List<PathSectionJGraph> pathSectionList = getPathSectionList(sections);
        pathSectionList.forEach(e -> graph.addEdge(e.getSource(), e.getTarget(), e));

        path = new DijkstraShortestPath<>(graph);
    }

    private Set<PathStation> getStations(List<PathSection> sections) {
        return sections.stream().flatMap(s -> Stream.of(s.getUpStation(), s.getDownStation())).collect(Collectors.toSet());
    }

    private List<PathSectionJGraph> getPathSectionList(List<PathSection> sections) {
        return sections
                .stream()
                .map(PathSectionJGraph::from)
                .collect(Collectors.toList());
    }

    @Override
    public SubwayPath search(PathStation source, PathStation target) {
        GraphPath<PathStation, PathSectionJGraph> path = this.path.getPath(source, target);

        return SubwayPath.of(Kilometer.of(path.getWeight()), path.getVertexList());
    }
}
