package nextstep.subway.line.service;

import nextstep.subway.line.dto.CreateLineRequest;
import nextstep.subway.line.repository.Line;

public interface LineSavable {
    Line saveLine(CreateLineRequest request);
}
