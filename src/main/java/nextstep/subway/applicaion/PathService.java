package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class PathService {

    private final StationRepository stationRepository;
    private final LineRepository lineRepository;
    private final PathFinder pathFinder;

    public PathService(final StationRepository stationRepository, final LineRepository lineRepository, final PathFinder pathFinder) {
        this.stationRepository = stationRepository;
        this.lineRepository = lineRepository;
        this.pathFinder = pathFinder;
    }

    public PathResponse shortestPath(final Long sourceStationId, final Long targetStationId) {
        if (sourceStationId.equals(targetStationId)) {
            throw new IllegalArgumentException();
        }

        Station sourceStation = stationRepository.findById(sourceStationId).orElseThrow(IllegalArgumentException::new);
        Station targetStation = stationRepository.findById(targetStationId).orElseThrow(IllegalArgumentException::new);
        List<Line> lines = lineRepository.findAll();

        GraphPath<Station, DefaultWeightedEdge> path = pathFinder.getPath(lines, sourceStation, targetStation);

        return new PathResponse(path.getVertexList(), path.getWeight());
    }
}
