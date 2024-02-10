package nextstep.subway.path.service;

import nextstep.subway.path.service.dto.PathResponse;
import nextstep.subway.path.service.dto.PathSearchRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PathService {
    public PathResponse findPath(final PathSearchRequest searchRequest) {
        return null;
    }
}
