package nextstep.subway.service;


import nextstep.subway.dto.path.PathResponse;
import nextstep.subway.entity.Section;
import nextstep.subway.entity.Sections;
import nextstep.subway.entity.Station;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;

public class PathFinder {
    public PathResponse getShortestPath(List<Sections> sectionsList, Station sourceStation, Station targetStation) {
        try {
            WeightedMultigraph<Station, DefaultWeightedEdge> graph = getGraph(sectionsList);

            GraphPath path = getPath(graph, sourceStation, targetStation);

            List<Station> shortestPath = path.getVertexList();
            Integer distance = (int) path.getWeight();

            return new PathResponse(shortestPath, distance);
        } catch (Exception e) {
            throw new IllegalArgumentException("출발역과 도착역이 연결되어 있어야 한다.");
        }
    }

    private WeightedMultigraph<Station, DefaultWeightedEdge> getGraph(List<Sections> sectionsList) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);

        for (Sections sections : sectionsList) {
            for (Section section : sections.getSections()) {
                graph.addVertex(section.getDownStation());
                graph.addVertex(section.getUpStation());

                DefaultWeightedEdge edge = graph.addEdge(section.getUpStation(), section.getDownStation());
                graph.setEdgeWeight(edge, section.getDistance());
            }
        }

        return graph;
    }

    private GraphPath getPath(WeightedMultigraph<Station, DefaultWeightedEdge> graph, Station sourceStation, Station targetStation) {
        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
        return dijkstraShortestPath.getPath(sourceStation, targetStation);
    }
}
