package nextstep.subway.applicaion.dto;

import java.util.List;

public class PathResult<T> {

    private final List<T> pathList;

    private final double distance;

    public List<T> getPathList() {
        return pathList;
    }

    public PathResult(List<T> pathList, double distance) {
        this.pathList = pathList;
        this.distance = distance;
    }

    public double getDistance() {
        return distance;
    }
}
