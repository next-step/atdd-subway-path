package atdd.path.service;

import atdd.path.application.dto.EdgeRequestView;
import atdd.path.application.dto.EdgeResponseView;
import atdd.path.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

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
        Edge savedEdge = edgeRepository.save(Edge.of(requestView));
        savedEdge.getSource().addEdgeToSource(savedEdge);
        savedEdge.getTarget().addEdgeToTarget(savedEdge);
        savedEdge.getLine().addEdgeToLine(savedEdge);
        stationRepository.save(savedEdge.getSource());
        stationRepository.save(savedEdge.getTarget());
        lineRepository.save(savedEdge.getLine());
        return EdgeResponseView.of(savedEdge);
    }

    public void deleteEdgesByStationId(Long lineId, Long id) {
        Station station = stationRepository.findById(id)
                .orElseThrow(NoSuchElementException::new);
        Optional<Line> line = lineRepository.findById(lineId);
        List<Edge> edges = line.get().getEdges();
        List<Edge> edgesAsSource = station.getEdgesAsSource();
        List<Edge> edgesAsTarget = station.getEdgesAsTarget();

        Long edgeIdWithNewTarget = 0L;
        if (edgesAsSource != null) {
            edgeIdWithNewTarget = edgesAsSource.get(0).getId();
            edgeRepository.deleteById(edgeIdWithNewTarget);
        }

        Long edgeIdWithNewSource = 0L;
        if (edgesAsTarget != null) {
            edgeIdWithNewSource = edgesAsTarget.get(0).getId();
            edgeRepository.deleteById(edgeIdWithNewSource);
        }

        mergeEdges(lineId, edgeIdWithNewSource, edgeIdWithNewTarget);
    }

    public Edge mergeEdges(Long lineId, Long edgeIdWithSource, Long edgeIdWithTarget) {
        Edge edgeWithSource = edgeRepository.findById(edgeIdWithSource).get();
        Station newSource = edgeWithSource.getSource();
        Edge edgeWithTarget = edgeRepository.findById(edgeIdWithTarget).get();
        Station newTarget = edgeWithTarget.getTarget();

        Line line = lineRepository.findById(lineId).orElseThrow(NoSuchElementException::new);

        Edge newEdge = Edge.builder()
                .source(newSource)
                .target(newTarget)
                .line(line)
                .distance(edgeWithSource.getDistance() + edgeWithTarget.getDistance())
                .timeToTake(edgeWithSource.getTimeToTake() + edgeWithTarget.getDistance())
                .build();
        Edge newSavedEdge = edgeRepository.save(newEdge);

        return newSavedEdge;
    }
}
