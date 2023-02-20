package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.path.PathFinder;
import nextstep.subway.domain.path.SubwayMap;
import nextstep.subway.domain.path.Path;
import nextstep.subway.domain.path.DijkstraPathFinder;
import nextstep.subway.domain.Station;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PathService {

    private final StationService stationService;
    private final LineService lineService;

    public PathService(StationService stationService, LineService lineService) {
        this.stationService = stationService;
        this.lineService = lineService;
    }

    @Transactional(readOnly = true)
    public PathResponse findShortestPath(Long departureStationId, Long DestinationStationId) {
        Station departureStation = stationService.findById(departureStationId);
        Station destinationStation = stationService.findById(DestinationStationId);
        List<Line> lines = lineService.findAll();

        SubwayMap subwayMap = new SubwayMap(lines, new WeightedMultigraph<>(DefaultWeightedEdge.class));

        PathFinder pathFinder = new DijkstraPathFinder(subwayMap);
        Path path = pathFinder.findPath(departureStation, destinationStation);

        return new PathResponse(path);
    }
}
