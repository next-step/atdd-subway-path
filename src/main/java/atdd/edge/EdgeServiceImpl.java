package atdd.edge;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class EdgeServiceImpl implements EdgeService {

    private final EdgeRepository edgeRepository;

    @Override
    public Edge create(Edge edge) {
        return edgeRepository.save(edge);
    }

    @Override
    public void delete(Edge edge) {
        edgeRepository.delete(edge);
    }
}
