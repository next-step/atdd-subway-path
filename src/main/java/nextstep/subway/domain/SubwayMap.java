package nextstep.subway.domain;

import java.util.List;
import nextstep.subway.exception.SourceAndTargetNotConnectedException;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

public class SubwayMap {

    private final DijkstraShortestPath dijkstraShortestPath;

    public SubwayMap(List<Line> lines) {
        WeightedMultigraph<Station, DefaultWeightedEdge> subwayMap = new WeightedMultigraph(DefaultWeightedEdge.class);

        for (Line line : lines) {
            Sections sections = line.getSections();
            sections.putSections(subwayMap);
        }

        this.dijkstraShortestPath = new DijkstraShortestPath(subwayMap);
    }


    public List<Station> getShortestPath(Station source, Station target) {
        try {
            return dijkstraShortestPath.getPath(source, target).getVertexList();
        } catch (NullPointerException e) {
            throw new SourceAndTargetNotConnectedException();
        }

    }

    public double getShortestDistance(Station source, Station target) {
        return dijkstraShortestPath.getPathWeight(source, target);
    }
}
