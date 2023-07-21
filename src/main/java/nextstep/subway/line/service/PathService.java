package nextstep.subway.line.service;

import lombok.RequiredArgsConstructor;
import nextstep.subway.line.dto.ShortestPathResponse;
import nextstep.subway.line.entity.LineRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PathService {

    private final LineRepository lineRepository;

    public ShortestPathResponse getShortestPath(long sourceStationId, long targetStationId) {
        return new ShortestPathResponse();
    }
}
