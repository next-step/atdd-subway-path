package atdd.station;

import atdd.edge.EdgeRepository;
import atdd.line.Line;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class StationServiceImpl implements StationService {

    private final StationRepository stationRepository;
    private final EdgeRepository edgeRepository;

    @Override
    public Station create(Station station) {
        return stationRepository.save(station);
    }

    @Override
    public void delete(Station station) {
        stationRepository.delete(station);
    }

    @Override
    public List<Station> findAll() {
        return stationRepository.findAll();
    }

    @Override
    public Set<Station> findBy(Line line) {
        return edgeRepository.findByLine(line).stream()
                .flatMap(e -> e.getStations().stream())
                .collect(Collectors.toSet());
    }

    @Override
    public Station findBy(Long id) {
        return stationRepository.findById(id).orElseThrow(RuntimeException::new);
    }
}
