package nextstep.subway.applicaion;

import lombok.RequiredArgsConstructor;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PathService {
    private final LineService lineService;
    private final StationService stationService;

    public PathResponse findPath(Long source, Long target) {
        Station sourceStation = stationService.findById(source);
        Station targetStation = stationService.findById(target);

        PathFinder pathFinder = new PathFinder(lineService.findAll(), sourceStation, targetStation);

        return new PathResponse(pathFinder.getPath(), pathFinder.getDistance());
    }
}
