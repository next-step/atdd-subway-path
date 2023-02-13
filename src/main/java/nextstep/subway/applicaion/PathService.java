package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.Station;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.springframework.stereotype.Service;

@Service
public class PathService {

    private final LineService lineService;

    private final StationService stationService;

    public PathService(LineService lineService, StationService stationService) {
        this.lineService = lineService;
        this.stationService = stationService;
    }

    public PathResponse findShortestPath(Long sourceId, Long targetId) {
        PathFinder pathFinder = new PathFinder(lineService.findAllLines());
        GraphPath<Station, DefaultWeightedEdge> shortestPath = pathFinder.getShortestPath(
            stationService.findById(sourceId),
            stationService.findById(targetId)
        );
        return PathResponse.of(shortestPath.getVertexList(), shortestPath.getWeight());
    }
}
