package nextstep.subway.path.application;

import nextstep.subway.exception.NotValidRequestException;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.path.dto.PathType;
import org.springframework.stereotype.Component;

@Component
public class PathFacade {

    private final PathService pathService;

    public PathFacade(PathService pathService) {
        this.pathService = pathService;
    }

    public PathResponse findPath(Long startStationId, Long endStationId, PathType type) {
        if (type.isDuration()) {
            return pathService.findFastestPath(startStationId, endStationId);
        }
        if (type.isDistance()) {
            return pathService.findShortestPath(startStationId, endStationId);
        }
        throw new NotValidRequestException("지원하지 않는 TYPE의 경로 조회 요청입니다.");
    }
}
