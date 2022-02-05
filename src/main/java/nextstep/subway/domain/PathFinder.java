package nextstep.subway.domain;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.exception.BusinessException;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.http.HttpStatus;

import java.util.List;

public class PathFinder {

    private PathFinder() {
    }

    public static PathResponse findPath(List<Line> lines, Station from, Station to) {
        pathValidCheck(from, to);
        WeightedMultigraph<Station, DefaultWeightedEdge> graph
                = new WeightedMultigraph(DefaultWeightedEdge.class);

        for (Line line : lines) {
            createGraph(line, graph);
        }

        return getPathResponse(from, to, graph);
    }

    private static void pathValidCheck(Station from, Station to) {
        if (from.getId().equals(to.getId())) {
            throw new BusinessException("출발지와 도착지가 같습니다", HttpStatus.BAD_REQUEST);
        }
    }

    private static PathResponse getPathResponse(Station from, Station to, WeightedMultigraph<Station, DefaultWeightedEdge> graph) {
        try {
            DijkstraShortestPath dijkstraShortestPath
                    = new DijkstraShortestPath(graph);
            List<Station> shortestPath
                    = dijkstraShortestPath.getPath(from, to).getVertexList();
            double pathWeight = dijkstraShortestPath.getPathWeight(from, to);
            return PathResponse.of(shortestPath, (int) pathWeight);
        } catch (Exception e) {
            throw new BusinessException("연결되지 않음", HttpStatus.BAD_REQUEST);
        }
    }

    private static void createGraph(Line line, WeightedMultigraph<Station, DefaultWeightedEdge> graph) {
        List<Section> sections = line.getSections().getSections();
        for (Section section : sections) {
            graph.addVertex(section.getUpStation());
            graph.addVertex(section.getDownStation());
            graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance());
        }
    }
}
