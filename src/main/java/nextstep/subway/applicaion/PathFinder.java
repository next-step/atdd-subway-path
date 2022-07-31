package nextstep.subway.applicaion;

import lombok.RequiredArgsConstructor;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.LineRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PathFinder {

    private final LineRepository lineRepository;

    public PathResponse solve(Long source, Long target) {
        return null;
    }
}
