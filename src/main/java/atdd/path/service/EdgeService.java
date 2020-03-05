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
        savedEdge.getSource().addLine(requestView.getLine());
        savedEdge.getTarget().addEdgeToTarget(savedEdge);
        savedEdge.getTarget().addLine(requestView.getLine());
        savedEdge.getLine().addEdgeToLine(savedEdge);
        return EdgeResponseView.of(savedEdge);
    }

    public void deleteEdgesByStationId(Long id) {
        //지하철역_아이디를_주_해당_역이_포함된_엣지를_삭제한다
        Station station = stationRepository.findById(id)
                .orElseThrow(NoSuchElementException::new);

        List<Edge> edgesAsSource = station.getEdgesAsSource();
        if (edgesAsSource != null) {
            for (Edge edge : edgesAsSource) {
                edgeRepository.deleteById(edge.getId());
            }
        }

        List<Edge> edgesAsTarget = station.getEdgesAsTarget();
        if (edgesAsTarget != null) {
            for (Edge edge : edgesAsTarget) {
                edgeRepository.deleteById(edge.getId());
            }
        }
    }

    public Edge mergeEdges(Long edgeIdWithSource, Long edgeIdWithTarget) {
        Edge edgeWithSource = edgeRepository.findById(edgeIdWithSource).get();
        Station newSource = edgeWithSource.getSource();
        Edge edgeWithTarget = edgeRepository.findById(edgeIdWithTarget).get();
        Station newTarget = edgeWithTarget.getTarget();

        Line line=new Line();
        for(Line tmp:newSource.getLines()){
            if(tmp.equals(newTarget.getLines())){
                line=tmp;
            }
        }

        Edge newEdge = Edge.builder()
                .source(newSource)
                .target(newTarget)
                .line(line)
                .distance(edgeWithSource.getDistance()+edgeWithTarget.getDistance())
                .timeToTake(edgeWithSource.getTimeToTake()+edgeWithTarget.getDistance())
                .build();
        Edge newSavedEdge = edgeRepository.save(newEdge);

        return newSavedEdge;
    }
}
