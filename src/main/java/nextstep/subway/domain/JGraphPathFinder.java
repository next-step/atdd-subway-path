package nextstep.subway.domain;

import nextstep.subway.applicaion.dto.PathResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class JGraphPathFinder implements PathFinder {

    @Override
    public PathResponse findPath(List<Line> lines, Station source, Station target) {
        //TODO 경로 생성 및 탐생
        return PathResponse.toResponse(0, new ArrayList<>());
    }
}
