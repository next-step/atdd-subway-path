package subway.domain.service;

import lombok.RequiredArgsConstructor;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Service;
import subway.domain.Station;
import subway.domain.StationLine;
import subway.exception.StationLineSearchFailException;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class StationShortestPathCalculateService {
    public ShortestStationPath calculateShortestPath(
            Station startStation,
            Station destinationStation,
            List<Station> stations,
            List<StationLine> stationLines) {

        final WeightedMultigraph<Long, DefaultWeightedEdge> graph = makeGraphFrom(stations, stationLines);

        final DijkstraShortestPath<Long, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);
        final GraphPath<Long, DefaultWeightedEdge> path = dijkstraShortestPath.getPath(startStation.getId(), destinationStation.getId());

        if (Objects.isNull(path)) {
            throw new StationLineSearchFailException("there is no path between start station and destination station");
        }

        return ShortestStationPath.builder()
                .stationIds(path.getVertexList())
                .distance(BigDecimal.valueOf(path.getWeight()))
                .build();
    }

    private WeightedMultigraph<Long, DefaultWeightedEdge> makeGraphFrom(List<Station> stations, List<StationLine> stationLines) {
        final WeightedMultigraph<Long, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);

        stations.stream()
                .map(Station::getId)
                .forEach(graph::addVertex);

        stationLines.stream()
                .map(StationLine::getSections)
                .flatMap(Collection::stream)
                .forEach(stationLineSection -> {
                    graph.setEdgeWeight(graph.addEdge(stationLineSection.getUpStationId(), stationLineSection.getDownStationId()), stationLineSection.getDistance().doubleValue());
                });

        return graph;
    }
}
