package nextstep.subway.domain;

import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.ArrayList;
import java.util.List;

public class Path {
    private List<Line> lines;

    public Path(List<Line> lines) {
        this.lines = lines;
    }

    public List<Long> getShortestPath(Long source, Long target) {
        List<Section> allSections = new ArrayList<>();

        lines.forEach(
                line -> {
                    allSections.addAll(line.getSections());
                }
        );

        WeightedMultigraph<Long, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);
        for (Section s : allSections) {
            graph.addVertex(s.getUpStationId());
            graph.addVertex(s.getDownStationId());
            graph.setEdgeWeight(graph.addEdge(s.getUpStationId(), s.getDownStationId()), s.getDistance());
        }

        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
        List<Long> vertexList = dijkstraShortestPath.getPath(source, target).getVertexList();

        return vertexList;
    }
}
