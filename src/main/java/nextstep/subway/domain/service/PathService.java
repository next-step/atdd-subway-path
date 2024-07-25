package nextstep.subway.domain.service;

import nextstep.subway.application.dto.PathResponse;

public interface PathService {
    PathResponse findPath(Long sourceId, Long targetId);
}
