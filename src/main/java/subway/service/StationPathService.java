package subway.service;

import lombok.RequiredArgsConstructor;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.domain.Station;
import subway.domain.StationLine;
import subway.domain.StationLineRepository;
import subway.domain.StationRepository;
import subway.service.dto.StationPathResponse;
import subway.service.dto.StationResponse;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StationPathService {
    private final StationLineRepository stationLineRepository;
    private final StationRepository stationRepository;

    @Transactional(readOnly = true)
    public StationPathResponse searchStationPath(Long startStationId, Long destinationStationId) {
        final List<Station> totalStations = stationRepository.findAll();
        final List<StationLine> totalStationLines = stationLineRepository.findAll();

        final WeightedMultigraph<Long, DefaultWeightedEdge> graph = makeGraphFrom(totalStations, totalStationLines);

        final DijkstraShortestPath<Long, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);
        final GraphPath<Long, DefaultWeightedEdge> path = dijkstraShortestPath.getPath(startStationId, destinationStationId);

        final Map<Long, Station> stationMap = totalStations.stream()
                .collect(Collectors.toMap(Station::getId, Function.identity()));

        final List<Long> pathStationIds = path.getVertexList();

        return StationPathResponse.builder()
                .stations(pathStationIds.stream()
                        .map(stationMap::get)
                        .map(StationResponse::fromEntity)
                        .collect(Collectors.toList()))
                .distance(BigDecimal.valueOf(path.getWeight()))
                .build();
    }

    private WeightedMultigraph<Long, DefaultWeightedEdge> makeGraphFrom(List<Station> totalStations, List<StationLine> totalStationLines) {
        final WeightedMultigraph<Long, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);

        totalStations.stream()
                .map(Station::getId)
                .forEach(graph::addVertex);

        totalStationLines.stream()
                .map(StationLine::getSections)
                .flatMap(Collection::stream)
                .forEach(stationLineSection -> {
                    graph.setEdgeWeight(graph.addEdge(stationLineSection.getUpStationId(), stationLineSection.getDownStationId()), stationLineSection.getDistance().doubleValue());
                });

        return graph;
    }

}
