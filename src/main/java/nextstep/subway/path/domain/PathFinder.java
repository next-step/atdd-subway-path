package nextstep.subway.path.domain;

import nextstep.subway.line.dto.LineResponse;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PathFinder {

    public ShortestPathResult findShortestPath(List<LineResponse> lines, Long startStationId, Long endStationId, PathFindType type) {
        return ShortestPathResult.empty();
    }
}
