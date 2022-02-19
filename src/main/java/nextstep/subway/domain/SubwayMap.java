package nextstep.subway.domain;

import java.util.List;
import nextstep.subway.exception.SourceAndTargetNotConnectedException;
import nextstep.subway.exception.SourceAndTargetSameException;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

public class SubwayMap {

    private final DijkstraShortestPath dijkstraShortestPath;

    public SubwayMap(List<Line> lines) {
        WeightedMultigraph<Station, DefaultWeightedEdge> subwayMap = new WeightedMultigraph(DefaultWeightedEdge.class);
        for (Line line : lines) {
            List<Section> sections = line.getSections();
            subwayMapAddStation(sections, subwayMap);
        }
        this.dijkstraShortestPath = new DijkstraShortestPath(subwayMap);
    }

    private void subwayMapAddStation(List<Section> sections, WeightedMultigraph<Station, DefaultWeightedEdge> subwayMap) {
        for (Section section : sections) {
            subwayMap.addVertex(section.getUpStation());
            subwayMap.addVertex(section.getDownStation());
            subwayMap.setEdgeWeight(subwayMap.addEdge(section.getUpStation(),
                    section.getDownStation()), section.getDistance());
        }
    }

    public List<Station> getShortestPath(Station source, Station target) {
        validStations(source, target);
        try {
            return dijkstraShortestPath.getPath(source, target).getVertexList();
        } catch (NullPointerException e) {
            throw new SourceAndTargetNotConnectedException();
        }
    }

    private void validStations(Station source, Station target) {
        if (source.equals(target)) {
            throw new SourceAndTargetSameException();
        }
    }

    public double getShortestDistance(Station source, Station target) {
        return dijkstraShortestPath.getPathWeight(source, target);
    }
}
