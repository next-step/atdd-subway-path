package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.Path;
import nextstep.subway.domain.PathFinder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PathService {

    private LineService lineService;
    private StationService stationService;

    public PathService(LineService lineService, StationService stationService) {
        this.lineService = lineService;
        this.stationService = stationService;
    }

    @Transactional(readOnly = true)
    public PathResponse getPath(Long source, Long target) {
        PathFinder pathFinder = new PathFinder(lineService.findLines());
        Path path = pathFinder.getShortestPath(stationService.findById(source), stationService.findById(target));
        return new PathResponse(path);
    }
}
