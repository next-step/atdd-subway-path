package atdd.station.domain;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import javax.persistence.*;
import java.util.List;
import java.util.stream.Collectors;


@Entity
public class Graph {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToMany
    @JoinColumn(name = "subway_line_id")
    private List<SubwayLine> subwayLines;

    public Graph() {
    }

    public Graph(List<SubwayLine> subwayLines) {
        this.subwayLines = subwayLines;
    }

    public List<SubwayLine> getSubwayLines() {
        return subwayLines;
    }

    public List<Station> getShortestDistancePath(long startId, long endId) {
        return getPathStations(makeGraph(subwayLines), startId, endId);
    }

    private WeightedMultigraph<Long, DefaultWeightedEdge> makeGraph(List<SubwayLine> lines) {
        WeightedMultigraph<Long, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        lines.stream()
                .flatMap(it -> it.getStations().stream())
                .forEach(it -> graph.addVertex(it.getId()));


        lines.stream()
                .flatMap(SubwayLine::getEdgesStream)
                .forEach(it -> graph.setEdgeWeight(graph.addEdge(it.getSourceStationId(), it.getTargetStationId()), it.getDistance()));

        return graph;
    }

    private List<Station> getPathStations(WeightedMultigraph<Long, DefaultWeightedEdge> graph, Long startId, Long endId) {
        GraphPath<Long, DefaultWeightedEdge> path = new DijkstraShortestPath<>(graph).getPath(startId, endId);

        return path.getVertexList().stream()
                .map(this::findStation)
                .collect(Collectors.toList());
    }

    private Station findStation(Long stationId) {
        return this.subwayLines.stream()
                .flatMap(it -> it.getStations().stream())
                .filter(it -> it.getId() == stationId)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}


