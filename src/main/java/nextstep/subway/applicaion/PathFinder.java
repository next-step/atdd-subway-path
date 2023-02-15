package nextstep.subway.applicaion;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Path;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PathFinder {
    private WeightedMultigraph<String, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);

    public PathFinder() {
    }

    public void initGraph(List<Line> lines) {
        lines.stream().map(Line::getSections).forEach(a -> addGraphValue(a));
    }

    private void addGraphValue(Sections sections) {
        for (Section section : sections.getSections()) {
            String vertex1 = String.valueOf(section.getUpStation().getId());
            String vertex2 = String.valueOf(section.getDownStation().getId());
            graph.addVertex(vertex1);
            graph.addVertex(vertex2);

            graph.setEdgeWeight(graph.addEdge(vertex1, vertex2), section.getDistance());
        }
    }

    public Path searchShortPath(Long sourceId, Long targetId){
        DijkstraShortestPath path = new DijkstraShortestPath(graph);

        List<String> stationIds = path.getPath(String.valueOf(sourceId), String.valueOf(targetId)).getVertexList();
        int weight = (int) path.getPath(String.valueOf(sourceId), String.valueOf(targetId)).getWeight();

        return new Path(stationIds.stream()
                .map(Long::valueOf)
                .collect(Collectors.toList()), weight);
    }
}
