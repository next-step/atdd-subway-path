package atdd.station.service;

import atdd.station.domain.Station;
import atdd.station.dto.PathResponseDto;
import atdd.station.dto.PathStation;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Component
public class ShortestPathFinder {

    private final WeightedMultigraphFactory weightedMultigraphFactory;

    public ShortestPathFinder(WeightedMultigraphFactory weightedMultigraphFactory) {
        this.weightedMultigraphFactory = weightedMultigraphFactory;
    }

    @Transactional(readOnly = true)
    public PathResponseDto findPath(Station startStation, Station endStation) {
        WeightedMultigraph<PathStation, DefaultWeightedEdge> graph = weightedMultigraphFactory.create(startStation, endStation);

        final PathStation startPathStation = PathStation.from(startStation);
        final PathStation endPathStation = PathStation.from(endStation);
        final List<PathStation> pathStations = findShortestPath(graph, startPathStation, endPathStation);
        return PathResponseDto.of(startStation.getId(), endStation.getId(), pathStations);
    }

    private List<PathStation> findShortestPath(WeightedMultigraph<PathStation, DefaultWeightedEdge> graph,
                                               PathStation startPathStation,
                                               PathStation endPathStation) {

        DijkstraShortestPath<PathStation, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);
        final GraphPath<PathStation, DefaultWeightedEdge> paths = dijkstraShortestPath.getPath(startPathStation, endPathStation);
        if (Objects.isNull(paths)) {
            return Collections.emptyList();
        }
        return paths.getVertexList();
    }

}
