package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LinePath;
import nextstep.subway.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class PathService {
    private LineService lineService;
    private StationService stationService;

    public PathService(LineService lineService, StationService stationService) {
        this.lineService = lineService;
        this.stationService = stationService;
    }

    public PathResponse getPathResponse(Long fromStationId, Long toStationId) {
        List<Line> allLines = lineService.showLines();
        LinePath linePath = new LinePath(allLines);
        Station fromStation = stationService.findById(fromStationId);
        Station toStation = stationService.findById(toStationId);

        return new PathResponse(linePath.getStations(fromStation, toStation), linePath.getShortestDistance(fromStation, toStation));
    }

}
