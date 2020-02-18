package atdd.station.domain;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;
import java.util.stream.Collectors;

public class SubwayMap {
    private List<SubwayLine> subwayLines;

    public SubwayMap(List<SubwayLine> subwayLines) {
        this.subwayLines = subwayLines;
    }

    public List<Station> getShortestPath(Long startStationId,
                                         Long destinationStationId) {

        return getPathStations(makeSubwayMap(subwayLines), startStationId, destinationStationId);
    }

    private List<Station> getPathStations(WeightedMultigraph<Long, DefaultWeightedEdge> subwayMap,
                                          Long startStationId,
                                          Long destinationStationId) {

        GraphPath<Long, DefaultWeightedEdge> path = new DijkstraShortestPath(subwayMap).getPath(startStationId, destinationStationId);

        return path.getVertexList().stream()
                .map(it -> findStations(it))
                .collect(Collectors.toList());
    }

    private WeightedMultigraph<Long, DefaultWeightedEdge> makeSubwayMap(List<SubwayLine> subwayLines) {
        WeightedMultigraph<Long, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);

        subwayLines.stream()
                .flatMap(it -> it.getSubwaySections().stream())
                .forEach(it -> {
                    Station sourceStation = it.getSourceStation();
                    Station targetStation = it.getTargetStation();

                    graph.addVertex(sourceStation.getId());
                    graph.addVertex(targetStation.getId());
                    graph.setEdgeWeight(graph.addEdge(sourceStation.getId(), targetStation.getId()), Double.valueOf(1.0));
                });

        return graph;
    }

    private Station findStations(Long stationId) {
        return subwayLines.stream()
                .flatMap(it -> it.getStations().stream())
                .filter(it -> it.getId() == stationId)
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }

}
