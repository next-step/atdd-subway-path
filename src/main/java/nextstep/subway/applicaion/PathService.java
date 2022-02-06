package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.GraphService;
import nextstep.subway.domain.StationRepository;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class PathService {

    private final LineRepository lineRepository;

    private final StationRepository stationRepository;

    private final GraphService graphService;

    public PathService(
            final LineRepository lineRepository,
            final StationRepository stationRepository,
            final GraphService graphService
    ) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
        this.graphService = graphService;
    }

    public PathResponse findShortedPath(Long sourceId, Long targetId) {
        var allLines = lineRepository.findAll();

        var sourceStation = stationRepository.findById(sourceId)
                .orElseThrow(() -> new IllegalArgumentException("등록되어 있지 않은 역입니다. 역 id: " + sourceId));
        var targetStation = stationRepository.findById(targetId)
                .orElseThrow(() -> new IllegalArgumentException("등록되어 있지 않은 역입니다. 역 id: " + targetId));

        var shortestPath = sourceStation.findShortestPathTo(graphService, targetStation, allLines);
        var shortestDistance = sourceStation.findShortestDistanceTo(graphService, targetStation, allLines);

        return new PathResponse(
                shortestPath.stream()
                        .map(StationResponse::from)
                        .collect(Collectors.toList()),
                shortestDistance
        );
    }
}
