package nextstep.subway.service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import nextstep.subway.domain.Path;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.PathResponse;
import nextstep.subway.dto.StationResponse;
import nextstep.subway.exception.IllegalPathException;

@Service
@AllArgsConstructor
public class PathService {
    private static final Path path = new Path();
    private final StationService stationService;

    public PathResponse getPath(Long sourceId, Long targetId) {
        //stations & distance를 리턴
        validatePath(sourceId, targetId);
        Station sourceStation = stationService.findById(sourceId);
        Station targetStation = stationService.findById(targetId);
        List<String> stationNames = path.getStationNamesAlongPath(sourceStation.getName(), targetStation.getName());
        List<StationResponse> stationResponses = stationNames.stream()
                                                             .map(stationService::findStationByName)
                                                             .collect(Collectors.toList());
        int distance = path.getShortestDistanceBetweenStations(sourceStation.getName(), targetStation.getName());
        return PathResponse.of(stationResponses, distance);
    }

    private void validatePath(Long sourceId, Long targetId) {
        if (Objects.equals(sourceId, targetId)) {
            throw new IllegalPathException("출발역과 도착역이 같습니다.");
        }
    }

    public void addPath(String sourceName, String targetName, int distance) {
        path.addConnectionBetweenStations(sourceName, targetName, distance);
    }
}
