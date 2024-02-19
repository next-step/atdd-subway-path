package nextstep.subway.application;

import nextstep.subway.application.dto.PathResult;
import nextstep.subway.entity.Line;
import nextstep.subway.entity.Section;
import nextstep.subway.entity.Station;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PathFinder {
    public PathResult findShortestPath(List<Line> lines, Station departureStation, Station arrivalStation) {

        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);

        for(Line line : lines) {
            for(Section section : line.getAllSections()) {
                Station upStation = section.getUpStation();
                Station downStation = section.getDownStation();

                graph.addVertex(upStation);
                graph.addVertex(downStation);
                graph.setEdgeWeight(graph.addEdge(upStation, downStation), section.getDistance());
            }
        }

        DijkstraShortestPath shortestPath = new DijkstraShortestPath<>(graph);
        GraphPath path = shortestPath.getPath(departureStation, arrivalStation);

        List vertexInOrder = path.getVertexList();
        int distance = (int) path.getWeight();

        return new PathResult(vertexInOrder, distance);
    }
}
