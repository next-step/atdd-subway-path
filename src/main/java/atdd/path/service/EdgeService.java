package atdd.path.service;

import atdd.path.domain.*;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class EdgeService {
    private LineRepository lineRepository;
    private EdgeRepository edgeRepository;
    private StationRepository stationRepository;

    public EdgeService(LineRepository lineRepository, EdgeRepository edgeRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.edgeRepository = edgeRepository;
        this.stationRepository = stationRepository;
    }

    public Edge addEdge(Long lineId, Long sourceId, Long targetId, int distance) {
        Line line = lineRepository.findById(lineId)
                .orElseThrow(NoSuchElementException::new);
        Station source = stationRepository.findById(sourceId)
                .orElseThrow(NoSuchElementException::new);
        Station target = stationRepository.findById(targetId)
                .orElseThrow(NoSuchElementException::new);

        Edge edge = Edge.builder()
                .line(line)
                .source(source)
                .target(target)
                .distance(10)
                .build();
        Edge save = edgeRepository.save(edge);
        return save;
    }
}
