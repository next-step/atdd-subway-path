package nextstep.subway.path;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import nextstep.subway.line.domain.Sections;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import nextstep.subway.support.ErrorCode;
import nextstep.subway.support.SubwayException;

public class DijkstraShortestPathFinder implements ShortestPathFinder {

    @Override
    public ShortestPath find(Sections sections, Station source, Station target) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = createGraph(sections);

        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
        GraphPath<Station, ShortestPath> result = dijkstraShortestPath.getPath(source, target);

        if (result == null) {
            throw new SubwayException(ErrorCode.PATH_SOURCE_TARGET_NOT_CONNECTED);
        }

        return new ShortestPath(result.getVertexList(), (int) result.getWeight());
    }

    private WeightedMultigraph<Station, DefaultWeightedEdge> createGraph(Sections sections) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);

        for (Section section : sections.getSections()) {
            if (!graph.containsVertex(section.getUpStation())) {
                graph.addVertex(section.getUpStation());
            }

            if (!graph.containsVertex(section.getDownStation())) {
                graph.addVertex(section.getDownStation());
            }

            graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance());
        }

        return graph;
    }
}
