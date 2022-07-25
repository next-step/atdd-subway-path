package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.Station;
import org.springframework.stereotype.Service;

@Service
public class PathService {

    private final LineService lineService;
    private final StationService stationService;

    public PathService(LineService lineService, StationService stationService) {
        this.lineService = lineService;
        this.stationService = stationService;
    }

    public PathResponse findShortestPath(Long source, Long target) {
        Station startStation = stationService.findById(source);
        Station arrivalStation = stationService.findById(target);
        return null;
    }
}
