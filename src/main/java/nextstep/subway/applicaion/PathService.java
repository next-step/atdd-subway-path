package nextstep.subway.applicaion;

import lombok.val;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.domain.object.Distance;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class PathService {
    private final StationRepository stationRepository;

    private final StationService stationService;
    private final LineService lineService;

    public PathService(StationRepository stationRepository, StationService stationService, LineService lineService) {
        this.stationRepository = stationRepository;
        this.stationService = stationService;
        this.lineService = lineService;
    }

    public PathResponse getShortestDistanceAndPath(Long sourceId, Long targetId) {
        val lines = lineService.findAll();
        val sourceStation = stationRepository.getById(sourceId);
        val targetStation = stationRepository.getById(targetId);

        val shortestPath = lines.getShortestPath(sourceStation, targetStation);
        val shortestDistance = lines.getShortestPathDistance(sourceStation, targetStation);

        return createResponse(shortestPath, shortestDistance);
    }

    private PathResponse createResponse(List<Station> shortestPath, Distance shortestDistance) {
        List<StationResponse> responses = stationService.createStationResponses(shortestPath);

        return new PathResponse(responses, shortestDistance);
    }
}
