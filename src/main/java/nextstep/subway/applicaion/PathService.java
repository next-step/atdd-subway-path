package nextstep.subway.applicaion;

import lombok.RequiredArgsConstructor;
import nextstep.subway.applicaion.dto.PathRequest;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.Lines;
import nextstep.subway.domain.Path;
import nextstep.subway.domain.PathGraph;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PathService {

    private final LineService lineService;

    public PathResponse findShortestPath(PathRequest pathRequest) {
        Lines lines = lineService.getLines();
        PathGraph pathGraph = PathGraph.valueOf(lines);
        Path shortestPath = pathGraph.findShortPath(pathRequest.getSource(), pathRequest.getTarget());
        return PathResponse.from(shortestPath);
    }
}
