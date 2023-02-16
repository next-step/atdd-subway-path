package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathResponse;
import org.springframework.stereotype.Service;

@Service
public class PathService {
    public PathResponse findShortestPath(Long departureStationId, Long DestinationStationId) {
        return new PathResponse();
    }
}
