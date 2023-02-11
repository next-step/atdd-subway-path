package nextstep.subway.applicaion;

import lombok.RequiredArgsConstructor;
import nextstep.subway.applicaion.dto.PathRequest;
import nextstep.subway.domain.Lines;
import nextstep.subway.domain.Path;
import nextstep.subway.domain.PathFinder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class PathService {

    private final LineService lineService;

    public Path findShortestPath(PathRequest request) {
        Lines lines = lineService.findByStationIds(request.toStationIds());
        PathFinder pathFinder = lines.toPathFinder();
        return pathFinder.findShortest(lines);
    }
}

