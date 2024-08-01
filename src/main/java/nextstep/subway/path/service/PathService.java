package nextstep.subway.path.service;

import nextstep.subway.line.entity.Line;
import nextstep.subway.path.dto.PathResponse;

import java.util.List;

public interface PathService {
    PathResponse findPath(Long source, Long target, List<Line> lineList);
}
