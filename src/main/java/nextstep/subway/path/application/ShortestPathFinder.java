package nextstep.subway.path.application;

import java.util.List;

import org.springframework.stereotype.Service;

import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.path.dto.ShortestPathResult;

@Service
public class ShortestPathFinder {

    public ShortestPathResult findShortestDistance(List<LineResponse> allLines, Long startId, Long endId,
        ShortestPathSearchType type) {
        return ShortestPathResult.empty();
    }
}
