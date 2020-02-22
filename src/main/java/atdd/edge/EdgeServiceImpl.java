package atdd.edge;

import atdd.line.Line;
import atdd.station.Station;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

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

    @Override
    public Set<Line> findLinesByStation(Station station) {
        return edgeRepository.findLinesByStation(station)
                .stream()
                .map(Edge::getLine)
                .collect(Collectors.toSet());
    }


}
