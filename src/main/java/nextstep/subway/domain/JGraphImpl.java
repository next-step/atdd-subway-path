package nextstep.subway.domain;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class JGraphImpl implements GraphStrategy {
    private final List<Line> lines;
    private final WeightedMultigraph<Station, DefaultWeightedEdge> graph;
    private List<Station> shortestPath;

    public JGraphImpl(List<Line> lines) {
        this.lines = lines;
        graph = new WeightedMultigraph(DefaultWeightedEdge.class);
        initGraph();
    }

    private List<Station> findAllDistinctStations() {
        return lines.stream()
                .map(Line::getStations)
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    private List<Section> findAllSections() {
        return lines.stream()
                .map(Line::getSections)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private void initVertex() {
        findAllDistinctStations()
                .forEach(graph::addVertex);
    }

    private void initEdgeWeight() {
        findAllSections()
                .forEach(this::setEdgeWeight);
    }

    private void setEdgeWeight(Section section) {
        Station upStation = section.getUpStation();
        Station downStation = section.getDownStation();
        Distance distance = section.getDistance();

        graph.setEdgeWeight(graph.addEdge(upStation, downStation), distance.getValue());
    }

    private void initGraph() {
        initVertex();
        initEdgeWeight();
    }


    @Override
    public List<Station> findShortestPath(Station target, Station source) {
        DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath(graph);
        GraphPath<Station, DefaultWeightedEdge> path = dijkstraShortestPath.getPath(target, source);

        return path.getVertexList();
    }

    @Override
    public int getShortestPathSize() {
        return 0;
    }
}
