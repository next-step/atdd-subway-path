package atdd.station.service;

import atdd.exception.ErrorType;
import atdd.exception.SubwayException;
import atdd.station.model.entity.Edge;
import atdd.station.repository.EdgeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EdgeService {
    @Autowired
    private EdgeRepository edgeRepository;

    public List<Edge> findAllById(Iterable<Long> ids) {
        return edgeRepository.findAllById(ids);
    }

    public void deleteById(long id) {
        edgeRepository.deleteById(id);
    }

    public Edge createEdge(final List<Edge> legacyEdges, final Edge newEdge) {
        boolean isConnect = legacyEdges.stream().anyMatch(data -> data.connectedEdge(newEdge));

        if (!legacyEdges.isEmpty() && !isConnect)
            throw new SubwayException(ErrorType.INVALID_EDGE);

        return edgeRepository.save(newEdge);
    }

    public Edge save(Edge edge) {
        return edgeRepository.save(edge);
    }
}

