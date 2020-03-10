package atdd.path.service;

import atdd.path.application.dto.LineResponseView;
import atdd.path.domain.Graph;
import atdd.path.domain.Line;
import atdd.path.domain.LineRepository;
import atdd.path.domain.Station;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GraphService {
    private LineService lineService;

    public GraphService(LineService lineService) {
        this.lineService = lineService;
    }

    public List<Station> findStationsInPath(Long startId, Long endId){
        List<LineResponseView> lineResponseViews = lineService.showAll();
        List<Line> lines = lineResponseViews.stream()
                .map(it -> Line.of(it))
                .collect(Collectors.toList());
        Graph graph = new Graph(lines);
        return graph.getStationsInShortestPath(startId, endId);
    }
}
