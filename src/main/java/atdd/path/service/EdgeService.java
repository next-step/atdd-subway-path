package atdd.path.service;

import atdd.path.application.dto.EdgeRequestView;
import atdd.path.application.dto.EdgeResponseView;
import atdd.path.domain.*;
import org.springframework.stereotype.Service;

@Service
public class EdgeService {
    private LineRepository lineRepository;
    private EdgeRepository edgeRepository;
    private StationRepository stationRepository;

    public EdgeService(LineRepository lineRepository, EdgeRepository edgeRepository,
                       StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.edgeRepository = edgeRepository;
        this.stationRepository = stationRepository;
    }

    public EdgeResponseView addEdge(EdgeRequestView requestView) throws Exception {
        Edge savedEdge = edgeRepository.save(Edge.builder()
                .line(requestView.getLine())
                .source(requestView.getSource())
                .target(requestView.getTarget())
                .distance(requestView.getDistance())
                .build());
        savedEdge.getSource().addEdgeToSource(savedEdge);
        savedEdge.getSource().addLine(requestView.getLine());
        savedEdge.getTarget().addEdgeToTarget(savedEdge);
        savedEdge.getTarget().addLine(requestView.getLine());
        savedEdge.getLine().addEdgeToLine(savedEdge);
        return EdgeResponseView.of(savedEdge);
    }
}
