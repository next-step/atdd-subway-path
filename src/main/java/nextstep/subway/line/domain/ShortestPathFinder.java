package nextstep.subway.line.domain;

import nextstep.subway.common.exception.ValidationException;
import nextstep.subway.line.domain.entity.Line;
import nextstep.subway.line.domain.entity.Section;
import nextstep.subway.station.entity.Station;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class ShortestPathFinder implements PathFinder{

    private final DijkstraShortestPath dijkstraShortestPath;
    private final GraphPath path;

    public ShortestPathFinder(List<Line> lineList, Station sourceStation, Station targetStation) {

        if (sourceStation.equals(targetStation)) {
            throw new ValidationException("station.0002");
        }

        List<Section> sectionList = new ArrayList<>();
        lineList.forEach(l -> sectionList.addAll(l.getSectionList()));

        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        sectionList.forEach(s -> {
            graph.addVertex(s.getUpStation());
            graph.addVertex(s.getDownStation());
            graph.setEdgeWeight(graph.addEdge(s.getUpStation(), s.getDownStation()), s.getDistance());
            graph.setEdgeWeight(graph.addEdge(s.getDownStation(), s.getUpStation()), s.getDistance());
        });
        this.dijkstraShortestPath = new DijkstraShortestPath(graph);
        try {
            this.path = this.dijkstraShortestPath.getPath(sourceStation, targetStation);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("path.0001");
        }
    }

    @Override
    public List<Station> getPath() {
        return this.path.getVertexList();
    }

    @Override
    public BigInteger getWeight() {
        return BigInteger.valueOf(Math.round(this.path.getWeight()));
    }
}
