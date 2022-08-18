package nextstep.subway.applicaion.dto;

import org.jgrapht.GraphPath;

import java.util.List;

public class PathResponse {

    private List<String> shortestPath;
    private List<GraphPath> paths;
    private Double distance;

    public PathResponse(List<String> shortestPath, List<GraphPath> paths, Double distance) {
        this.shortestPath = shortestPath;
        this.paths = paths;
        this.distance = distance;
    }

    public List<GraphPath> getPaths() {
        return paths;
    }

    public List<String> getShortestPath() {
        return shortestPath;
    }

    public Double getDistance() {
        return distance;
    }
}
