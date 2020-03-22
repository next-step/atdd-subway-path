package atdd.path.serivce;

import atdd.path.domain.Edge;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EdgeService {
    public Edge create(Edge edge) {
        return null;
    }
    public List<Edge> findEdgesByStationId(Long stationId) {
        return null;
    }

    public Edge createEdgeForMerge(Long lineId, Long stationId, List<Edge> oldEdges) {
        return null;
    }

    public void deleteOldEdges(List<Edge> oldEdges) {
    }
}
