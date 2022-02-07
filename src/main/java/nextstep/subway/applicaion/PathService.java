package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Lines;
import nextstep.subway.domain.StationDijkstraShortestStationPathFinder;
import org.jgrapht.GraphPath;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PathService {
    private LineRepository lineRepository;

    public PathService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
    }

    public PathResponse getPaths(Long source, Long target) {
        Lines lines = new Lines(lineRepository.findAll());
        StationDijkstraShortestStationPathFinder stationDijkstraShortestStationPathFinder = StationDijkstraShortestStationPathFinder.ofLines(lines);

        final GraphPath findPath = stationDijkstraShortestStationPathFinder.getPath(source.toString(), target.toString());
        List<String> vertexList = findPath.getVertexList();
        List<Long> ids = convertStringToLong(vertexList);

        List<StationResponse> stations = lines.getStationsByIds(ids)
            .stream()
            .map(StationResponse::ofStation)
            .collect(Collectors.toList());
        int distance = (int) Math.round(findPath.getWeight());

        return new PathResponse(stations, distance);
    }

    private List<Long> convertStringToLong(List<String> vertexList) {
        return vertexList.stream()
            .map(Long::parseLong)
            .collect(Collectors.toList());
    }
}
