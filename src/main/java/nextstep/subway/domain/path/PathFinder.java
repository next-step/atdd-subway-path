package nextstep.subway.domain.path;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.path.PathBadRequestException;
import nextstep.subway.exception.path.PathNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PathFinder {
    private PathFinderStrategy pathFinderStrategy;

    public PathFinder(PathFinderStrategy pathFinderStrategy) {
        this.pathFinderStrategy = pathFinderStrategy;
    }

    public Path findShortestPathAndItsDistance(List<Line> lines, Station sourceStation, Station targetStation) {
        validatePathFinderParams(lines, sourceStation, targetStation);

        return pathFinderStrategy.findShortestPathAndItsDistance(lines, sourceStation, targetStation);
    }

    private void validatePathFinderParams(List<Line> lines, Station sourceStation, Station targetStation) {
        if (lines.isEmpty()) {
            throw new PathNotFoundException("구간을 조회할 수 있는 노선이 존재하지 않습니다.");
        }
        if (sourceStation.equals(targetStation)) {
            throw new PathBadRequestException("출발역과 도착역은 동일할 수 없습니다.");
        }
    }
}
