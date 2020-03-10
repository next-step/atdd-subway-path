package atdd.path.domain;

import com.sun.tools.internal.ws.wsdl.framework.NoSuchEntityException;
import lombok.Getter;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class Graph {
    private List<Line> lines;

    public Graph(List<Line> lines) {
        this.lines = lines;
    }

    public List<Station> getStationsInShortestPath(Long startId, Long endId) {
        return getShortestPathStations(makeGraph(lines), startId, endId);
    }

    private WeightedMultigraph<Long, DefaultWeightedEdge> makeGraph(List<Line> lines) {
        WeightedMultigraph<Long, DefaultWeightedEdge> graph
                = new WeightedMultigraph<Long, DefaultWeightedEdge>(DefaultWeightedEdge.class);

        lines.stream()
                .flatMap(it -> it.getStations().stream())
                .forEach(it -> graph.addVertex(it.getId()));

        lines.stream()
                .flatMap(it -> it.getEdges().getEdges().stream())
                .forEach(it -> graph.setEdgeWeight(graph.addEdge(it.getSource().getId(), it.getTarget().getId()), it.getDistance()));

        return graph;
    }

    private List<Station> getShortestPathStations(WeightedMultigraph<Long, DefaultWeightedEdge> graph, Long startId, Long endId) {
        GraphPath<Long, DefaultWeightedEdge> path = new DijkstraShortestPath(graph).getPath(startId, endId);
        List<Station> collect = path.getVertexList()
                .stream()
                .map(it -> findStation(it))
                .collect(Collectors.toList());
        return collect;
    }

    private Station findStation(Long id) {
        Station station = lines.stream()
                .flatMap(it -> it.getStations().stream())
                .filter(it -> it.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NoSuchEntityException("해당 역이 없습니다."));
        return station;
    }
}
