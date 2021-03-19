package nextstep.subway.path.application;

import nextstep.subway.line.dto.LineSectionResponse;
import nextstep.subway.path.exception.SeperatedStationsException;
import nextstep.subway.station.dto.StationResponse;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.alg.shortestpath.KShortestPaths;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;
import java.util.stream.Collectors;

public class ShortestPathFinder {

    private final WeightedMultigraph<Long, DefaultWeightedEdge> graph;
    private final List<StationResponse> stationResponses;
    private final List<LineSectionResponse> lineSectionResponses;

    public ShortestPathFinder(List<StationResponse> stationResponses, List<LineSectionResponse> lineSectionResponses) {
        this.stationResponses = stationResponses;
        this.lineSectionResponses = lineSectionResponses;

        graph = new WeightedMultigraph(DefaultWeightedEdge.class);
        setStations();
        setSections();
    }

    public List<StationResponse> getShortestPath(Long sourceStationId, Long targetStationId) {
        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
        GraphPath path = dijkstraShortestPath.getPath(sourceStationId, targetStationId);

        if (path == null) {
            throw new SeperatedStationsException();
        }

        List<Long> shortestPath = path.getVertexList();
        return shortestPath.stream()
                .map(id -> stationResponses.stream().filter(it -> it.getId().equals(id)).findFirst().orElseThrow(RuntimeException::new))
                .collect(Collectors.toList());
    }

    public int getShortestDistance(Long sourceStationId, Long targetStationId) {
        List<GraphPath> paths = new KShortestPaths(graph, 100).getPaths(sourceStationId, targetStationId);
        return (int) paths.get(0).getWeight();
    }

    private void setStations() {
        stationResponses.forEach(it -> graph.addVertex(it.getId()));
    }

    private void setSections() {
        lineSectionResponses.forEach(this::setSection);
    }

    private void setSection(LineSectionResponse lineSectionResponse) {
        lineSectionResponse.getSections()
                .forEach(it -> graph.setEdgeWeight(
                        graph.addEdge(it.getUpStationId(), it.getDownStationId()),
                        it.getDistance()
                        )
                );
    }
}
