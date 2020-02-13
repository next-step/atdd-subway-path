package atdd.line.service;

import atdd.global.exception.ServiceNotFoundException;
import atdd.line.domain.Edge;
import atdd.line.domain.EdgeRepository;
import atdd.line.domain.Line;
import atdd.line.domain.LineRepository;
import atdd.station.domain.Station;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class LineService {

    private final LineRepository lineRepository;
    private final EdgeRepository edgeRepository;

    @Transactional
    public Line saveLine(Line line) {
        return lineRepository.save(line);
    }

    @Transactional
    public Line saveEdge(Edge edge) {
        final Edge savedEdge = edgeRepository.save(edge);
        return savedEdge.getLine();
    }

    public List<Line> findAll() {
        return lineRepository.findAll();
    }

    public Line findLineById(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> new ServiceNotFoundException("지하철 노선이 존재하지 않습니다.", Map.of("id", id)));
    }

    @Transactional
    public void deleteLineById(Long id) {
        final Line findLine = findLineById(id);
        lineRepository.deleteById(findLine.getId());
    }

    @Transactional
    public void deleteEdge(Long lineId, Long stationId) {
        final List<Edge> edges = edgeRepository.findEdges(lineId, stationId);

        if (!CollectionUtils.isEmpty(edges)) {
            final Edge firstEdge = edges.get(0);
            final Station targetStation = firstEdge.getTargetStation();

            edgeRepository.deleteEdgeById(firstEdge.getId());

            changeTargetStationInSecondEdge(edges, targetStation);
        }
    }

    public List<Edge> findEdgesByStationId(Long stationId) {
        return edgeRepository.findEdgesByStationId(stationId);
    }

    public List<Edge> findEdgesByLineId(Long lineId) {
        return edgeRepository.findEdgesByLineId(lineId);
    }

    private void changeTargetStationInSecondEdge(List<Edge> edges, Station targetStation) {
        if (2 == edges.size()) {
            edges.get(1).changeTargetStation(targetStation);
        }
    }

}
