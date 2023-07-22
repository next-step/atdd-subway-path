package nextstep.subway.line.algorithm;

import nextstep.subway.line.entity.Line;
import nextstep.subway.line.entity.Section;
import nextstep.subway.station.entity.Station;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Component
public class ShortestPathFinder {

    public List<Station> searchPath(List<Line> lineList, Station source, Station target) {
        List<Section> sectionList = new ArrayList<>();
        lineList.forEach(l -> sectionList.addAll(l.getSectionList()));

        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        sectionList.forEach(s -> {
            graph.addVertex(s.getUpStation());
            graph.addVertex(s.getDownStation());
            graph.setEdgeWeight(graph.addEdge(s.getUpStation(), s.getDownStation()), s.getDistance());
            graph.setEdgeWeight(graph.addEdge(s.getDownStation(), s.getUpStation()), s.getDistance());
        });
        return new DijkstraShortestPath(graph).getPath(source, target).getVertexList();
    }

    public BigInteger getShortestWeight(List<Line> lineList, Station source, Station target) {
        List<Section> sectionList = new ArrayList<>();
        lineList.forEach(l -> sectionList.addAll(l.getSectionList()));

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
