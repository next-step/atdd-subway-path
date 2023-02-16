package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.response.PathResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.PathFinder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class PathService {
    private final LineRepository lineRepository;

    public PathService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public PathResponse shortestPath(Long source, Long target) {
        List<Line> lines = lineRepository.findAll();
        PathFinder pathFinder = new PathFinder(new Dikstra(), lines);
        return PathResponse.of(pathFinder.shortestPath(source, target));
    }
}
