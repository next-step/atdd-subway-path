package atdd.station.service;

import atdd.station.exception.ErrorType;
import atdd.station.exception.SubwayException;
import atdd.station.model.entity.Edge;
import atdd.station.model.entity.Line;
import atdd.station.model.entity.Station;
import atdd.station.repository.LineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class LineService {
    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private StationService stationService;

    @Autowired
    private EdgeService edgeService;

    public Line create(final Line line) {
        return lineRepository.save(line);
    }

    public Line findById(final long id) {
        return lineRepository.findById(id).orElseThrow(() -> new SubwayException(ErrorType.NOT_FOUND_LINE));
    }

    public List<Line> findAll() {
        return lineRepository.findAll();
    }

    public void deleteById(final long id) {
        lineRepository.deleteById(id);
    }

    public Line addEdge(final long id, final Edge newEdge){
        final Line line = findById(id);
        final List<Edge> legacyEdges = edgeService.findAllById(line.getEdgeIds());

        addNewEdge(legacyEdges, newEdge);

        updateLineWithEdgesAndStations(legacyEdges, newEdge, line);

        return lineRepository.save(line);
    }

    private void updateLineWithEdgesAndStations(final List<Edge> legacyEdges, final Edge newEdge, final Line line) {
        final List<Edge> newEdges = new ArrayList<>();
        final Set<Long> stationIds = new HashSet<>();

        stationIds.add(newEdge.getTargetStationId());
        stationIds.add(newEdge.getSourceStationId());

        if (legacyEdges.isEmpty())
            newEdges.add(newEdge);

        for (Edge legacyEdge : legacyEdges) {
            stationIds.add(legacyEdge.getSourceStationId());
            stationIds.add(legacyEdge.getTargetStationId());

            if (legacyEdge.connectTargetAndSource(newEdge)) {
                newEdges.add(legacyEdge);
                newEdges.add(newEdge);

                continue;
            } else if (legacyEdge.connectSourceAndTarget(newEdge)) {
                newEdges.add(newEdge);
                newEdges.add(legacyEdge);

                continue;
            }

            newEdges.add(legacyEdge);
        }

        List<Station> stationList = updateLineInStations(stationIds, line.getId());

        line.updateEdge(newEdges, stationList);
    }

    public void deleteEdge(final long id, final long stationId) {
        final Line line = findById(id);
        final Station station = stationService.findById(stationId);

        // 노선에서 구간 삭제
        final List<Edge> legacyEdges = edgeService.findAllById(line.getEdgeIds());

        final List<Edge> newEdges = new ArrayList<>();
        final Set<Long> newStationIds = new HashSet<>();

        int edgeIndex = 0;
        int continueIndex = -1;
        for (Edge legacyEdge : legacyEdges) {
            if (continueIndex > 0 && continueIndex == edgeIndex) {
                continueIndex = -1;

                edgeService.deleteById(legacyEdge.getId());
                continue;
            }

            if (legacyEdge.getSourceStationId() == station.getId() || legacyEdge.getTargetStationId() == station.getId()) {
                if (newEdges.isEmpty() && legacyEdge.getSourceStationId() == station.getId()) {
                    edgeService.deleteById(legacyEdge.getId());
                    continue;
                }

                if (edgeIndex == (legacyEdges.size() - 1) && legacyEdge.getTargetStationId() == station.getId()) {
                    edgeService.deleteById(legacyEdge.getId());
                    continue;
                }

                if (legacyEdge.getTargetStationId() == station.getId()) {
                    continueIndex = edgeIndex + 1;

                    Edge nextLegacyEdge = legacyEdges.get(continueIndex);
                    Edge newEdge = Edge.builder()
                            .sourceStationId(legacyEdge.getSourceStationId())
                            .targetStationId(nextLegacyEdge.getTargetStationId()).build();

                    edgeService.save(newEdge);

                    newEdges.add(newEdge);
                    newStationIds.add(newEdge.getSourceStationId());
                    newStationIds.add(newEdge.getTargetStationId());
                }

                edgeService.deleteById(legacyEdge.getId());
                continue;
            }

            newEdges.add(legacyEdge);
            newStationIds.add(legacyEdge.getSourceStationId());
            newStationIds.add(legacyEdge.getTargetStationId());

            edgeIndex++;
        }

        List<Station> stationList = stationService.findAllById(newStationIds);

        line.updateEdge(newEdges, stationList);
        lineRepository.save(line);

        // 지하철역에서 라인 삭제
        deleteLineInStation(station, line.getId());
    }

    private List<Station> updateLineInStations(final Set<Long> stationIds, final long lineId) {
        List<Station> stationList = stationService.findAllById(stationIds);

        for (Station station : stationList) {
            Set<Long> lineIds = new HashSet<>(station.getLineIds());
            lineIds.add(lineId);

            station.setLineIds(new ArrayList<>(lineIds));
        }

        return stationService.saveAll(stationList);
    }

    // TODO 이 부분 먼저 바꿔보자!
    public List<Edge> getEdges(Line line) {
        List<Edge> edges = line.getLineEdges();

        if (edges.isEmpty()) {
            return edgeService.findAllById(line.getEdgeIds());
        }

        return edges;
    }

    public List<Station> getStations(Line line, List<Edge> edges) {
        List<Station> stations = line.getLineStations();

        if (stations.isEmpty()) {
            Set<Long> stationIds = new HashSet<>();

            for (Edge edge : edges) {
                stationIds.addAll(edge.stationIds());
            }

            return stationService.findAllById(stationIds);
        }

        return stations;
    }

    private void addNewEdge(final List<Edge> legacyEdges, final Edge newEdge) throws SubwayException {
        boolean isConnect = legacyEdges.stream().anyMatch(data -> data.connectedEdge(newEdge));

        if (!legacyEdges.isEmpty() && !isConnect)
            throw new SubwayException(ErrorType.INVALID_EDGE);

        edgeService.save(newEdge);
    }

    private Station deleteLineInStation(final Station station, final long deleteLineId) {
        int index = 0;
        int removeLineIndex = -1;
        for (Long lineId : station.getLineIds()) {
            if (lineId == deleteLineId)
                removeLineIndex = index;

            index++;
        }

        if (removeLineIndex > -1)
            station.getLineIds().remove(removeLineIndex);

        return stationService.save(station);
    }
}
