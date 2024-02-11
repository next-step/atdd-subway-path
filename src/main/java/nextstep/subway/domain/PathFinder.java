package nextstep.subway.domain;

import java.util.List;
import nextstep.subway.applicaion.dto.PathRequest;
import nextstep.subway.applicaion.dto.PathResponse;

public interface PathFinder {
    PathResponse findPath(PathRequest pathRequest, List<Line> lines);
    void validateRequest(PathRequest request, List<Line> lines);
}
