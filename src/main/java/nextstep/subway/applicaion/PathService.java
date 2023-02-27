package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PathService {

    @Transactional(readOnly = true)
    public PathResponse getPath() {
        return null;
    }
}
