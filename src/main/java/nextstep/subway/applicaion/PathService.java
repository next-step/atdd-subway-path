package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Lines;
import nextstep.subway.domain.StationDijkstraShortestStationPathFinder;
import nextstep.subway.exception.PathNotValidException;
import org.jgrapht.GraphPath;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.exception.ErrorMessages.DUPLICATE_START_END_STATION;
import static nextstep.subway.exception.ErrorMessages.NOT_EXIST_START_END_STATION;

@Service
public class PathService {
    private LineRepository lineRepository;

    public PathService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
    }

    public PathResponse getPaths(Long source, Long target) {
        Lines lines = new Lines(lineRepository.findAll());

        checkParameters(lines, source, target);

        StationDijkstraShortestStationPathFinder stationDijkstraShortestStationPathFinder = StationDijkstraShortestStationPathFinder.ofLines(lines);
        final GraphPath findPath = stationDijkstraShortestStationPathFinder.getPath(source.toString(), target.toString());

        boolean isNotConnected = findPath == null;
        if (isNotConnected) {
            throw new PathNotValidException();
        }

        List<String> vertexList = findPath.getVertexList();
        List<Long> ids = convertStringToLong(vertexList);

        List<StationResponse> stations = lines.getStationsByIds(ids)
            .stream()
            .map(StationResponse::ofStation)
            .collect(Collectors.toList());
        int distance = (int) Math.round(findPath.getWeight());

        return new PathResponse(stations, distance);
    }

    private void checkParameters(Lines lines, Long source, Long target) {
        if (source.equals(target)) {
            throw new PathNotValidException(DUPLICATE_START_END_STATION.getMessage());
        }

        if (lines.isNotExistStations(source, target)) {
            throw new PathNotValidException(NOT_EXIST_START_END_STATION.getMessage());
        }
    }

    private List<Long> convertStringToLong(List<String> vertexList) {
        return vertexList.stream()
            .map(Long::parseLong)
            .collect(Collectors.toList());
    }
}
