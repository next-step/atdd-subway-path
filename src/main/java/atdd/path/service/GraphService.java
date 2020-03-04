package atdd.path.service;

import atdd.line.domain.Edge;
import atdd.line.domain.Line;
import atdd.line.service.LineService;
import atdd.path.domain.Graph;
import atdd.station.domain.Station;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class GraphService {

    private final LineService lineService;

    public List<Station> findDistancePath(long startId, long endId) {
        Graph graph = getGraph();
        return graph.getShortestPath(startId, endId, Edge::getDistance);
    }

    public List<Station> findTimePath(long startId, long endId) {
        Graph graph = getGraph();
        return graph.getShortestPath(startId, endId, Edge::getElapsedTime);
    }

    private Graph getGraph() {
        List<Line> lines = lineService.findLineWithEdgeAll();
        return new Graph(lines);
    }

}
