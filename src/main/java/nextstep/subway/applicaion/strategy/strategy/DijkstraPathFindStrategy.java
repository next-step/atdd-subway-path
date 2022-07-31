package nextstep.subway.applicaion.strategy.strategy;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.enums.SubwayErrorMessage;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;
import java.util.Objects;


public class DijkstraPathFindStrategy implements PathFindStrategy{
    private final WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
    private final DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);

    public DijkstraPathFindStrategy(List<Station> vertices, List<Section> edgeWeight) {
        addVertex(vertices);
        setEdgeWeight(edgeWeight);
    }

    @Override
    public List<Station> findShortPath(Station source, Station target) {
        validPath(source, target);
        return getPath(source, target).getVertexList();
    }

    @Override
    public int getShortestDistance(Station source, Station target) {
        validPath(source, target);
        return (int) getPath(source, target).getWeight();
    }


    private void addVertex(List<Station> stations) {
        for (Station station : stations) {
            graph.addVertex(station);
        }
    }

    private void setEdgeWeight(List<Section> sections) {
        for(Section section : sections) {
            graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistanceIntValue());
        }
    }

    private void validPath(Station source, Station target) {
        if(isUnconnected(source, target)) {
            throw new IllegalArgumentException(SubwayErrorMessage.UNCONNECTED_PATH.getMessage());
        }
        if (isSameSourceAndDestination(source, target)) {
            throw new IllegalArgumentException(SubwayErrorMessage.SAME_SOURCE_AND_DESTINATION.getMessage());
        }
    }

    private boolean isSameSourceAndDestination(Station source, Station target) {
        return source.equals(target);
    }

    private boolean isUnconnected(Station source, Station target) {
        return Objects.isNull(getPath(source, target));
    }

    private GraphPath<Station, DefaultWeightedEdge> getPath(Station source, Station target) {
        return dijkstraShortestPath.getPath(source, target);
    }
}
