package atdd.path.service;

import atdd.path.application.dto.EdgeRequestView;
import atdd.path.domain.Edge;
import atdd.path.domain.EdgeRepository;
import atdd.path.domain.LineRepository;
import atdd.path.domain.StationRepository;
import org.springframework.stereotype.Service;

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

    public Edge createEdge(EdgeRequestView requestView) throws Exception{
        if(requestView.getSourceId() == requestView.getTargetId()){
            throw new IllegalArgumentException("출발역과 도착역이 같으면 안 됩니다.");
        }
        Edge savedEdge = edgeRepository.save(EdgeRequestView.of(requestView));
        return savedEdge;
    }

    public void deleteEdge(Long id) {
        Optional<Edge> edge = edgeRepository.findById(id);
        edgeRepository.deleteById(id);
    }
}
