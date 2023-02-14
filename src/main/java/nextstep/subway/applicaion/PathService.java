package nextstep.subway.applicaion;

import lombok.RequiredArgsConstructor;
import nextstep.subway.applicaion.dto.PathRequest;
import nextstep.subway.domain.Lines;
import nextstep.subway.domain.Path;
import nextstep.subway.domain.PathFinder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class PathService {

    private final LineService lineService;
    private final PathFinder pathFinder;
    private final StationService stationService;

    public Path findShortestPath(PathRequest request) {
        Lines lines = lineService.findByStationIds(request.toStationIds());
        return pathFinder.searchShortestPath(request, lines.mergeSections());
    }
}

