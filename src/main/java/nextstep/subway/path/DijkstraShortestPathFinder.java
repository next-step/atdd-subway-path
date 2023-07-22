package nextstep.subway.path;

import java.util.ArrayList;
import java.util.List;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import nextstep.subway.line.domain.Sections;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;

public class DijkstraShortestPathFinder implements ShortestPathFinder {

    @Override
    public ShortestPath find(Sections sections, Station source, Station target) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = createGraph(sections);

        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
        GraphPath<Station, ShortestPath> result = dijkstraShortestPath.getPath(source, target);

        return new ShortestPath(result.getVertexList(), (int) result.getWeight());
    }

    private WeightedMultigraph<Station, DefaultWeightedEdge> createGraph(Sections sections) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);

        for (Section section : sections.getSections()) {
            graph.addVertex(section.getUpStation());
            graph.addVertex(section.getDownStation());
            graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance());
        }

        return graph;
    }

    @Deprecated
    private <T> List<Edge<Station>> toEdges(List<Station> vertexList) {
        List<Edge<Station>> vertices = new ArrayList<>();

        for (int sourceIdx = 0, targetIdx = sourceIdx + 1; targetIdx < vertexList.size(); sourceIdx++, targetIdx++) {
            vertices.add(new SectionEdge(vertexList.get(sourceIdx), vertexList.get(targetIdx)));
        }

        return vertices;
    }

    @Deprecated
    private Sections edgesToSections(Sections sections, List<Edge<Station>> edges) {
        List<Section> shortedSections = new ArrayList<>();

        for (Edge<Station> edge : edges) {
            shortedSections.add(sections.findSectionByStations(edge.getSource(), edge.getTarget()).get());
        }

        return new Sections(shortedSections);
    }
}
