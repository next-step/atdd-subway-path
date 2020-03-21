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

    public List<Edge> findAll() {
        return edgeRepository.findAll();
    }

    public List<Edge> findAllById(Iterable<Long> ids) {
        return edgeRepository.findAllById(ids);
    }

    public void deleteById(long id) {
        edgeRepository.deleteById(id);
    }

    public void deleteAll(List<Edge> edges) {
        edgeRepository.deleteAll(edges);
    }

    public Edge save(Edge edge) {
        return edgeRepository.save(edge);
    }

    public List<Edge> saveAll(List<Edge> edges) {
        return edgeRepository.saveAll(edges);
    }
}

