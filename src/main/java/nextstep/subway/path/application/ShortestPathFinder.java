package nextstep.subway.path.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.path.exception.SeperatedStationsException;
import nextstep.subway.station.domain.Station;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.alg.shortestpath.KShortestPaths;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;
import java.util.stream.Collectors;

public class ShortestPathFinder {

    private final WeightedMultigraph<Long, DefaultWeightedEdge> graph;
    private final List<Station> stations;
    private final List<Line> lines;

    public ShortestPathFinder(List<Station> stations, List<Line> lines) {
        this.stations = stations;
        this.lines = lines;

        graph = new WeightedMultigraph(DefaultWeightedEdge.class);
        setStations();
        setSections();
    }

    public List<Station> getShortestPath(Long sourceStationId, Long targetStationId) {
        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
        GraphPath path = dijkstraShortestPath.getPath(sourceStationId, targetStationId);

        if (path == null) {
            throw new SeperatedStationsException();
        }

        List<Long> shortestPath = path.getVertexList();
        return shortestPath.stream()
                .map(id -> stations.stream().filter(it -> it.getId().equals(id)).findFirst().orElseThrow(RuntimeException::new))
                .collect(Collectors.toList());
    }

    public int getShortestDistance(Long sourceStationId, Long targetStationId) {
        List<GraphPath> paths = new KShortestPaths(graph, 100).getPaths(sourceStationId, targetStationId);
        return (int) paths.get(0).getWeight();
    }

    private void setStations() {
        stations.forEach(it -> graph.addVertex(it.getId()));
    }

    private void setSections() {
        lines.forEach(this::setSection);
    }

    private void setSection(Line line) {
        line.getSections()
                .getSections()
                .forEach(it -> graph.setEdgeWeight(
                        graph.addEdge(it.getUpStation().getId(), it.getDownStation().getId()),
                        it.getDistance()
                        )
                );
    }
}
