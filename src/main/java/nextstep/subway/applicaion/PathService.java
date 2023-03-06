package nextstep.subway.applicaion;

import lombok.RequiredArgsConstructor;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.Path;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.util.ShortestPath;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PathService {

    private final LineService lineService;

    private final StationService stationService;

    public PathResponse findPath(Long sourceStationId, Long targetStationId) {
        Path path = new Path(lineService.findAll());

        ShortestPath shortestPath = path.findShortestPath(sourceStationId, targetStationId);
        List<Station> stations = stationService.findStationsByIdIn(shortestPath.getVertexList());

        return PathResponse.from(shortestPath.getVertexList(), stations, shortestPath.getWeight());
    }
}
