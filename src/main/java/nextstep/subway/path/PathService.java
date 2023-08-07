package nextstep.subway.path;

import nextstep.subway.exception.BadRequestException;
import nextstep.subway.graph.GraphService;
import nextstep.subway.station.Station;
import nextstep.subway.station.StationConverter;
import nextstep.subway.station.StationService;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class PathService {
    private final GraphService graphService;
    private final StationService stationService;
    private final StationConverter stationConverter;

    public PathService(GraphService graphService, StationService stationService, StationConverter stationConverter) {
        this.graphService = graphService;
        this.stationService = stationService;
        this.stationConverter = stationConverter;
    }

    public PathResponse getShortestPath(Long source, Long target) {
        ShortestPath<Station> shortestPath = new ShortestPath(graphService.getGraph(),
                stationService.getStation(source),
                stationService.getStation(target));
        validatePath(shortestPath);
        var stations = shortestPath.getVertexList()
                .stream()
                .map(e -> stationConverter.convert(e))
                .collect(Collectors.toList());
        return new PathResponse(stations, shortestPath.getShortestDistance());
    }

    private static void validatePath(ShortestPath<Station> shortestPath) {
        if (!shortestPath.isFound())
            throw new BadRequestException("the path is not found.");
    }
}
