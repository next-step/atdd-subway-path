package nextstep.subway.domain;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.exception.PathException;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.util.ObjectUtils;

public class PathFinder {

    private WeightedMultigraph<Station, DefaultWeightedEdge> graph;
    private DijkstraShortestPath dijkstraShortestPath;

    public PathFinder(List<Section> sectionList) {
        graph = new WeightedMultigraph(DefaultWeightedEdge.class);
        addVertex(graph, getStationList(sectionList));
        addEdgeWeight(graph, sectionList);
        dijkstraShortestPath = new DijkstraShortestPath(graph);
    }

    public List<Station> getShortesPath(Station startStation, Station finishStation) {
        GraphPath graphPath = dijkstraShortestPath.getPath(startStation, finishStation);
        if (ObjectUtils.isEmpty(graphPath) || graphPath.getEdgeList().isEmpty()) {
            throw new PathException("연결될 수 없는 구조입니다.");
        }

        return graphPath.getVertexList();
    }

    public long getSumOfDistance(Station startStation, Station finishStation) {
        return (long) dijkstraShortestPath.getPathWeight(startStation, finishStation);
    }

    private List<Station> getStationList(List<Section> sectionList) {
        return sectionList.stream()
            .flatMap(
                section -> Arrays.asList(section.getUpStation(), section.getDownStation()).stream())
            .distinct()
            .collect(Collectors.toList());
    }

    private void addVertex(WeightedMultigraph<Station, DefaultWeightedEdge> graph,
        List<Station> stationList) {
        stationList.forEach(station -> graph.addVertex(station));
    }

    private void addEdgeWeight(WeightedMultigraph<Station, DefaultWeightedEdge> graph,
        List<Section> sectionList) {
        sectionList.forEach(section -> {
            graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()),
                section.getDistance());
        });
    }

}
