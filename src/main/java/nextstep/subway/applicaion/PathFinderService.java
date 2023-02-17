package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PathFinderService {

    public PathResponse getShortestPath(Long source, Long target) {
        return null;
    }
}
