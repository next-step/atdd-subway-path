package subway.domain.service;

import lombok.RequiredArgsConstructor;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Service;
import subway.domain.Station;
import subway.domain.StationLineSection;
import subway.exception.StationLineSearchFailException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class StationShortestPathCalculateService {
    public ShortestStationPath calculateShortestPath(
            Station startStation,
            Station destinationStation,
            List<StationLineSection> stationLineSections) {

        final WeightedMultigraph<Long, DefaultWeightedEdge> graph = makeGraphFrom(stationLineSections);

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

    private WeightedMultigraph<Long, DefaultWeightedEdge> makeGraphFrom(List<StationLineSection> stationLineSections) {
        final WeightedMultigraph<Long, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);

        stationLineSections.forEach(stationLineSection -> {
            final Long upStationId = stationLineSection.getUpStationId();
            final Long downStationId = stationLineSection.getDownStationId();
            final BigDecimal distance = stationLineSection.getDistance();

            graph.addVertex(upStationId);
            graph.addVertex(downStationId);
            graph.setEdgeWeight(graph.addEdge(upStationId, downStationId), distance.doubleValue());
        });

        return graph;
    }
}
