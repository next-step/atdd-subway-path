package nextstep.subway.line.algorithm;


import nextstep.subway.line.entity.Line;
import nextstep.subway.station.entity.Station;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;

public class ShortestPathFinder {

    WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
    DijkstraShortestPath dijkstraShortestPath;

    public ShortestPathFinder(List<Line> lineList) {
        lineList.stream()
                .forEach(line -> {
                    line.getStations()
                            .forEach(station -> graph.addVertex(station));
                });

        this.dijkstraShortestPath = new DijkstraShortestPath(graph);

        lineList.forEach(line -> {
                    line.getSectionList()
                            .forEach(section -> graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance()));
        });
    }

    public void getPath(int source, int target) {

    }

    public void getWeight() {

    }
}
