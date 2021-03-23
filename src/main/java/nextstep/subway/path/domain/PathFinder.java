package nextstep.subway.path.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.path.dto.PathResponse;

import java.util.List;

public class PathFinder {

    private List<Line> lines;

    public PathFinder(List<Line> lines){
        this.lines = lines;
    }

    public PathResponse getShortestPath(Long source, Long target){
        return null;
    }

}
