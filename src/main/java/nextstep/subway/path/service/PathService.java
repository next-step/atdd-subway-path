package nextstep.subway.path.service;

import nextstep.subway.line.service.LineProvider;
import nextstep.subway.path.service.dto.PathResponse;
import nextstep.subway.path.service.dto.PathSearchRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PathService {

    private LineProvider lineProvider;

    public PathService(final LineProvider lineProvider) {
        this.lineProvider = lineProvider;
    }

    public PathResponse findPath(final PathSearchRequest searchRequest) {
        return null;
    }
}
