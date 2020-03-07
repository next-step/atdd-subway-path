package atdd.line.service;

import atdd.global.exception.ServiceNotFoundException;
import atdd.line.domain.Edge;
import atdd.line.domain.EdgeRepository;
import atdd.line.domain.Line;
import atdd.line.domain.LineRepository;
import atdd.path.domain.Edges;
import atdd.station.domain.Station;
import atdd.station.service.StationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class LineService {

    private final StationService stationService;

    private final LineRepository lineRepository;
    private final EdgeRepository edgeRepository;

    @Transactional
    public Line saveLine(Line line) {
        return lineRepository.save(line);
    }

    public List<Line> findLineWithEdgeAll() {
        return lineRepository.findLineWithEdgeAll();
    }

    public Optional<Line> findLineById(Long id) {
        return lineRepository.findById(id);
    }

    public Optional<Line> findLineWithEdgeById(Long id) {
        return lineRepository.findLineWithEdgeById(id);
    }

    @Transactional
    public void deleteLineById(Long id) {
        final Line findLine = findLineById(id)
                .orElseThrow(() -> new ServiceNotFoundException("지하철 노선이 존재하지 않습니다.", Map.of("id", id)));
        lineRepository.deleteById(findLine.getId());
    }

    @Transactional
    public Line saveEdge(Long lineId, Long sourceStationId, Long targetStationId, int elapsedTime, int distance) {
        final Line findLine = getLineWithEdgeById(lineId);
        final Station sourceStation = getStationById(sourceStationId);
        final Station targetStation = getStationById(targetStationId);

        final Edge edge = Edge.builder()
                .line(findLine)
                .sourceStation(sourceStation)
                .targetStation(targetStation)
                .elapsedTime(elapsedTime)
                .distance(distance)
                .build();
        findLine.addEdge(edge);

        final Edge savedEdge = edgeRepository.save(edge);
        return savedEdge.getLine();
    }

    @Transactional
    public void deleteEdgeStation(Long lineId, Long stationId) {
        final Station findStation = getStationById(stationId);
        final Line findLine = getLineWithEdgeById(lineId);

        final List<Edge> oldEdges = findLine.getEdges();
        final Edges edges = findLine.removeStation(findStation);
        final Edge newEdge = edges.getEdges().stream()
                .filter(edge -> !oldEdges.contains(edge))
                .findFirst().orElseThrow(RuntimeException::new);

        edgeRepository.deleteEdgeByLineIdAndStationId(findLine.getId(), findStation.getId());

        newEdge.changeLine(findLine);
        edgeRepository.save(newEdge);
    }

    private Line getLineWithEdgeById(Long lineId) {
        return findLineWithEdgeById(lineId)
                .orElseThrow(() -> new ServiceNotFoundException("지하철 노선이 존재하지 않습니다.", Map.of("id", lineId)));
    }

    private Station getStationById(Long stationId) {
        return stationService.findStationById(stationId)
                .orElseThrow(() -> new ServiceNotFoundException("지하철 역이 존재하지 않습니다.", Map.of("id", stationId)));
    }

}
