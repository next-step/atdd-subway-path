package atdd.path.service;

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
        List<Line> lines = lineService.findLineWithEdgeAll();
        Graph graph = new Graph(lines);
        return graph.getShortestDistancePath(startId, endId);
    }

}
