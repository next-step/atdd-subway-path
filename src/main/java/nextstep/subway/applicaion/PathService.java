package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.LinePath;
import nextstep.subway.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        Station fromStation = stationService.findById(fromStationId);
        Station toStation = stationService.findById(toStationId);
        LinePath linePath = new LinePath(lineService.showLines());

        return new PathResponse(fromStation.getName(), toStation.getName(), linePath.getShortestDistance(fromStation, toStation));
    }

}
