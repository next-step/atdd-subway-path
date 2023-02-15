package nextstep.subway.applicaion;

import java.util.List;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.Station;
import org.jgrapht.GraphPath;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
public class PathFinderService {

    private final PathFinder pathFinder;
    private final LineRepository lineRepository;

    public PathFinderService(final PathFinder pathFinder, final LineRepository lineRepository) {
        this.pathFinder = pathFinder;
        this.lineRepository = lineRepository;
    }

    @Cacheable(cacheNames = "graph")
    public void initGraph() {
        List<Line> lines = lineRepository.findAll();

        pathFinder.initGraph(lines);
    }

    public GraphPath find(final Station sourceStation, final Station targetStation) {
        return pathFinder.find(sourceStation, targetStation);
    }
}
