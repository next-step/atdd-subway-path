package nextstep.subway.domain.path;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.PathException;
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

        Path path = pathFinderStrategy.findShortestPathAndItsDistance(lines, sourceStation, targetStation);
        if (path.getStations().isEmpty()) {
            throw new PathException("주어진 출발역과 도착역은 연결된 구간이 없습니다.");
        }
        return path;
    }

    private void validatePathFinderParams(List<Line> lines, Station sourceStation, Station targetStation) {
        if (lines.isEmpty()) {
            throw new PathException("구간을 조회할 수 있는 노선이 존재하지 않습니다.");
        }
        if (sourceStation.equals(targetStation)) {
            throw new PathException("출발역과 도착역은 동일할 수 없습니다.");
        }
    }
}
