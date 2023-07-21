package nextstep.subway.line.algorithm;

import nextstep.subway.line.entity.Section;
import nextstep.subway.station.entity.Station;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.math.BigInteger;
import java.util.List;

public class DijkstraUtils {

    private DijkstraUtils() {}

    public static List<Station> searchPath(List<Section> sectionList, Station source, Station target) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        sectionList.forEach(s -> {
            graph.addVertex(s.getUpStation());
            graph.addVertex(s.getDownStation());
            graph.setEdgeWeight(graph.addEdge(s.getUpStation(), s.getDownStation()), s.getDistance());
            graph.setEdgeWeight(graph.addEdge(s.getDownStation(), s.getUpStation()), s.getDistance());
        });
        return new DijkstraShortestPath(graph).getPath(source, target).getVertexList();
    }

    public static BigInteger getShortestWeight(List<Section> sectionList, Station source, Station target) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        sectionList.forEach(s -> {
            graph.addVertex(s.getUpStation());
            graph.addVertex(s.getDownStation());
            graph.setEdgeWeight(graph.addEdge(s.getUpStation(), s.getDownStation()), s.getDistance());
            graph.setEdgeWeight(graph.addEdge(s.getDownStation(), s.getUpStation()), s.getDistance());
        });
        double weight = new DijkstraShortestPath(graph).getPath(source, target).getWeight();
        return BigInteger.valueOf(Math.round(weight));
    }
}
