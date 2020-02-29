package atdd.station.service;

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

    public Edge save(Edge edge) {
        return edgeRepository.save(edge);
    }
}

