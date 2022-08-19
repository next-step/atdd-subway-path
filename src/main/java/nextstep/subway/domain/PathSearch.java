package nextstep.subway.domain;

import nextstep.subway.applicaion.dto.PathResponse;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.alg.shortestpath.KShortestPaths;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.Comparator;
import java.util.List;

public class PathSearch {

    private WeightedMultigraph<String, DefaultWeightedEdge> graph;

    public PathSearch() {
        graph = new WeightedMultigraph(DefaultWeightedEdge.class);
    }

    /**
     *
     * @param lines
     */
    public void addPaths(List<Line> lines) {
        for (Line line : lines) {
            addStations(line.getStations());
            addSections(line.getSections());
        }
    }

    /**
     *
     * @param stations
     */
    private void addStations(List<Station> stations) {
        stations.forEach(station -> addStation(station));
    }

    private void addStation(Station station) {
        if (!graph.containsVertex(station.getName())) {
            graph.addVertex(station.getName());
        }
    }

    /**
     *
     * @param sections
     */
    private void addSections(Sections sections) {
        for (Section section : sections.getSections()) {
            String upStation = section.getUpStation().getName();
            String downStation = section.getDownStation().getName();
            graph.setEdgeWeight(graph.addEdge(upStation, downStation), section.getDistance());
        }
    }

    /**
     *
     * @param
     * @param
     * @return
     */
    public PathResponse findShortestPath(Station departure, Station destination) {
        // 예외처리 필요 (교재 예외상황 예시 참고)
        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
        List<String> shortestPath = dijkstraShortestPath.getPath(departure.getName(), destination.getName()).getVertexList();
        List<GraphPath> paths = new KShortestPaths(graph, 100).getPaths(departure.getName(), destination.getName());

        GraphPath graphPath = paths.stream().min(Comparator.comparingDouble(GraphPath::getWeight)).get();

        return new PathResponse(shortestPath, paths, graphPath.getWeight());
    }




}
