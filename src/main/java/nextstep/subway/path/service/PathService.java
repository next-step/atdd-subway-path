package nextstep.subway.path.service;

import nextstep.subway.exception.NotFoundLineException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.path.presentation.response.FindPathResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PathService {

    private LineRepository lineRepository;
    private StationRepository stationRepository;
    private PathFinder pathFinder;

    public PathService(LineRepository lineRepository, StationRepository stationRepository, PathFinder pathFinder) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
        this.pathFinder = pathFinder;
    }

    public FindPathResponse findShortestPath(Long startStationId, Long endStationId) {
        List<Line> lines = lineRepository.findAll();
        Station startStation = stationRepository.findById(startStationId)
                .orElseThrow(NotFoundLineException::new);
        Station endStation = stationRepository.findById(endStationId)
                .orElseThrow(NotFoundLineException::new);

        WeightedMultigraph<Station, DefaultWeightedEdge> graph = pathFinder.makeGraph(lines);
        GraphPath shortestPath = pathFinder.findShortestPath(graph, lines, startStation, endStation);

        return makePathToResponse(shortestPath);
    }

    private FindPathResponse makePathToResponse(GraphPath shortestPath) {
        List<Station> shortestPathStations = shortestPath.getVertexList();
        double shortestPathWeight = shortestPath.getWeight();

        return FindPathResponse.of(shortestPathStations, (int) shortestPathWeight);
    }

}
