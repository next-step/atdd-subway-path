package nextstep.subway.path.application;

import nextstep.subway.exception.NotValidRequestException;
import nextstep.subway.path.dto.PathResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Transactional
public class PathService {

    @Transactional(readOnly = true)
    public PathResponse findShortestPath(Long startStationId, Long endStationId) {
        if (Objects.equals(startStationId, endStationId)) {
            throw new NotValidRequestException("출발역과 도착역은 같을 수 없습니다.");
        }
        return null;
    }
}
