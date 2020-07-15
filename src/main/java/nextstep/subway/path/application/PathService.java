package nextstep.subway.path.application;

import nextstep.subway.exception.NotValidRequestException;
import nextstep.subway.line.application.LineService;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineStationResponse;
import nextstep.subway.path.domain.PathMap;
import nextstep.subway.path.dto.PathResponse;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
public class PathService {

    private final LineService lineService;

    public PathService(LineService lineService) {
        this.lineService = lineService;
    }

    @Transactional(readOnly = true)
    public PathResponse findShortestPath(Long startStationId, Long endStationId) {
        assertNotEqualsIds(startStationId, endStationId);

        List<Line> allLines = lineService.findAllLineEntities();
        PathMap pathMap = PathMap.of(allLines);

        List<Long> shortestPath = pathMap.findDijkstraShortestPath(startStationId, endStationId);

        return null;
    }

    private void assertNotEqualsIds(Long startStationId, Long endStationId) {
        if (Objects.equals(startStationId, endStationId)) {
            throw new NotValidRequestException("출발역과 도착역은 같을 수 없습니다.");
        }
    }
}
