package nextstep.subway.path.service;

import nextstep.subway.line.domain.Line;
import nextstep.subway.path.presentation.response.FindPathResponse;
import nextstep.subway.station.domain.Station;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PathFinder {

    public FindPathResponse findShortestPath(List<Line> lines, Station startStation, Station endStation) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);

        addVertex(lines, graph);
        setEdgeWeight(lines, graph);

        GraphPath shortestPath = new DijkstraShortestPath(graph).getPath(startStation, endStation);
        List<Station> shortestPathStations = shortestPath.getVertexList();
        double shortestPathWeight = shortestPath.getWeight();

        return FindPathResponse.of(shortestPathStations, (int) shortestPathWeight);
    }

    private void addVertex(List<Line> lines, WeightedMultigraph graph) {
        lines.stream()
                .flatMap(line -> line.getStations().stream())
                .forEach(graph::addVertex);
    }

    private void setEdgeWeight(List<Line> lines, WeightedMultigraph graph) {
        lines.stream()
                .flatMap(line -> line.getSections().stream())
                .forEach(section -> graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance()));
    }


}
