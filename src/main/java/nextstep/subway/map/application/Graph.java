package nextstep.subway.map.application;

import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.map.dto.PathResult;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Graph {

    public PathResult findPath(List<LineResponse> lineResponses, Long start, Long target) {
        return null;
    }

}
