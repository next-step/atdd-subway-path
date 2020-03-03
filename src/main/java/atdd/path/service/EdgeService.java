package atdd.path.service;

import atdd.path.domain.*;
import org.springframework.stereotype.Service;

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

    public Edge addEdge(Line line, Station source, Station target) throws Exception {
        Edge savedEdge = edgeRepository.save(
                Edge.builder()
                        .line(line)
                        .source(source)
                        .target(target)
                        .build());
        source.addEdgeToSource(savedEdge);
        source.addLine(line);
        target.addEdgeToTarget(savedEdge);
        target.addLine(line);
        line.addEdgeToLine(savedEdge);
        return savedEdge;
    }
}
