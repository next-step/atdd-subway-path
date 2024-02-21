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
    private final List<Sections> sectionsList;

    public PathFinder(List<Sections> sectionsList) {
        this.sectionsList = sectionsList;
    }

    public PathResponse getShortestPath(Station sourceStation, Station targetStation) {
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
            setGraph(graph, sections);
        }

        return graph;
    }

    private GraphPath getPath(WeightedMultigraph<Station, DefaultWeightedEdge> graph, Station sourceStation, Station targetStation) {
        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
        return dijkstraShortestPath.getPath(sourceStation, targetStation);
    }

    private void setGraph(WeightedMultigraph<Station, DefaultWeightedEdge> graph, Sections sections) {
        for (Section section : sections.getSections()) {
            addVertex(graph, section);
            setEdgeWeight(graph, section);
        }
    }

    private void addVertex(WeightedMultigraph<Station, DefaultWeightedEdge> graph, Section section) {
        graph.addVertex(section.getDownStation());
        graph.addVertex(section.getUpStation());
    }

    private void setEdgeWeight(WeightedMultigraph<Station, DefaultWeightedEdge> graph, Section section) {
        DefaultWeightedEdge edge = graph.addEdge(section.getUpStation(), section.getDownStation());
        graph.setEdgeWeight(edge, section.getDistance());
    }
}
