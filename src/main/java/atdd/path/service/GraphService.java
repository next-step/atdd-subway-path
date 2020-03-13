package atdd.path.service;

import atdd.path.application.dto.LineResponseView;
import atdd.path.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GraphService {
    private LineService lineService;

    public GraphService(LineService lineService) {
        this.lineService = lineService;
    }

    public List<Station> findStationsInShortestPath(Long startId, Long endId) {
        List<LineResponseView> lineResponseViews = lineService.showAll();
        List<Line> lines = lineResponseViews.stream()
                .map(it -> Line.of(it))
                .collect(Collectors.toList());
        Graph graph = new Graph(lines);
        return graph.getShortestDistancePath(startId, endId);
    }
}