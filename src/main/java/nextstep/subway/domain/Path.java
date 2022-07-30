package nextstep.subway.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;


@AllArgsConstructor
public class Path {
    private List<Station> stations;
    private List<Section> sections;
    private StationWeightedMultigraph graph;

    public Path(List<Station> stations, List<Section> sections) {
        this(stations, sections, new StationWeightedMultigraph());
    }

    public List<Station> getShortestWithDijkstra(Station source, Station target) {
        graph.setGraph(stations, sections);
        StationDijkstraShortestPath dijkstraShortestPath = new StationDijkstraShortestPath(graph, source, target);
        return dijkstraShortestPath.getVertexList();
    }

    public int getShortestDistance(Station source, Station target) {
        graph.setGraph(stations, sections);
        StationDijkstraShortestPath dijkstraShortestPath = new StationDijkstraShortestPath(graph, source, target);
        return dijkstraShortestPath.getWeight();
    }
}
