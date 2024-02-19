package nextstep.subway.path.service;

import nextstep.subway.exception.NotFoundLineException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.path.presentation.response.FindPathResponse;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.service.dto.ShowStationDto;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

        return pathFinder.findShortestPath(lines, startStation, endStation);
    }

}
