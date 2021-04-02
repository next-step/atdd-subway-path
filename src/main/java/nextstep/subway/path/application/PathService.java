package nextstep.subway.path.application;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.domain.Line;
import nextstep.subway.path.domain.PathFinder;
import nextstep.subway.path.dto.ShortestPathResponse;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional
public class PathService {

    private final StationService stationService;

    private final LineService lineService;

    private PathFinder pathFinder;

    public PathService(StationService stationService, LineService lineService) {
        this.stationService = stationService;
        this.lineService = lineService;
        List<Station> stations = stationService.findAll();
        List<Line> lines = lineService.findLines();

        pathFinder = new PathFinder(stations, lines);
    }

    public ShortestPathResponse getShortestPath(long source, long target) {
        int shortestLength = pathFinder.getShortestPathLength(source, target);
        List<Station> shortestPaths = pathFinder.getShortestPathList(source, target);
        return new ShortestPathResponse(StationResponse.ofList(shortestPaths), shortestLength);
    }
}
