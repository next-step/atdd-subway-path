package nextstep.subway.path.application;

import nextstep.subway.path.dto.PathResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@Transactional
public class PathService {

    public PathService() {

    }

    public PathResponse findShortestPath(Long sourceId, Long targetId) {
        return new PathResponse(Collections.emptyList(), 0);
    }
}
