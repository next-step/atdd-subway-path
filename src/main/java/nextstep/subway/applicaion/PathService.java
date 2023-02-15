package nextstep.subway.applicaion;

import nextstep.subway.domain.Path;
import nextstep.subway.domain.ShortestPath;
import nextstep.subway.domain.Station;
import nextstep.subway.ui.response.PathResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class PathService {
    private final LineService lineService;
    private final StationService stationService;

    public PathService(LineService lineService, StationService stationService) {
        this.lineService = lineService;
        this.stationService = stationService;
    }

    public PathResponse findShortestPath(Long sourceStationId, Long targetStationId) {
        Path path = new Path(lineService.findAllLines());

        ShortestPath shortestPath = path.findShortestPath(sourceStationId, targetStationId);
        List<Station> stations = stationService.findStationsByIdIn(shortestPath.getVertexList());

        return PathResponse.from(shortestPath.getVertexList(), stations, shortestPath.getWeight());
    }
}
