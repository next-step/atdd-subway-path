package atdd.station.service;

import atdd.exception.ErrorType;
import atdd.exception.SubwayException;
import atdd.station.model.entity.Edge;
import atdd.station.model.entity.Line;
import atdd.station.model.entity.Station;
import atdd.station.repository.LineRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class LineService {
    private LineRepository lineRepository;
    private StationService stationService;
    private EdgeService edgeService;

    public LineService(LineRepository lineRepository, StationService stationService, EdgeService edgeService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
        this.edgeService = edgeService;
    }

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

    public Line addEdge(final long id, Edge newEdge) {
        final Line line = findById(id);
        final List<Edge> legacyEdges = edgeService.findAllById(line.getEdgeIds());

        newEdge = edgeService.createEdge(legacyEdges, newEdge);
        line.addNewLineEdges(legacyEdges, newEdge);

        // update station
        final Set<Long> stationIds = stationIdsInEdges(line.getLineEdges());
        List<Station> stationList = stationService.updateLine(stationIds, line.getId());

        line.setLineStations(stationList);

        return lineRepository.save(line);
    }

    private Set<Long> stationIdsInEdges(final List<Edge> edges) {
        final Set<Long> stationIds = new HashSet<>();

        edges.stream().forEach(data -> {
            stationIds.addAll(data.getStationIds());
        });

        return stationIds;
    }

    public void deleteEdge(final long id, final long stationId) {
        final Line line = findById(id);
        final Station deleteStation = stationService.findById(stationId);

        // 노선에서 구간 삭제
        deleteEdgeInLine(line, deleteStation);
        lineRepository.save(line);

        // 지하철역에서 라인 삭제
        deleteLineInStation(deleteStation, line.getId());
    }

    public List<Edge> getLineEdges(Line line) {
        List<Edge> edges = line.getLineEdges();

        if (edges.isEmpty() && !line.getEdgeIds().isEmpty()) {
            return edgeService.findAllById(line.getEdgeIds());
        }

        return edges;
    }

    public List<Station> getLineStations(Line line, List<Edge> edges) {
        List<Station> stations = line.getLineStations();

        if (stations.isEmpty()) {
            Set<Long> stationIds = new HashSet<>();

            for (Edge edge : edges) {
                stationIds.addAll(edge.getStationIds());
            }

            return stationService.findAllById(stationIds);
        }

        return stations;
    }

    private Station deleteLineInStation(final Station station, final long deleteLineId) {
        Iterator iterator = station.getLineIds().iterator();

        while (iterator.hasNext()) {
            long value = (Long) iterator.next();

            if (value == deleteLineId) {
                iterator.remove();
                break;
            }
        }

        return stationService.save(station);
    }

    private void deleteEdgeInLine(final Line line, final Station deleteStation) {
        final List<Edge> legacyEdges = edgeService.findAllById(line.getEdgeIds());

        if (legacyEdges.isEmpty())
            return;

        final List<Edge> sortEdges = new ArrayList<>();
        final Set<Long> deleteEdgeIds = deleteFirstEdgeOrLastEdge(legacyEdges, deleteStation.getId());

        int edgeIndex = 0;
        int deleteIndex = -1;

        for (Edge legacyEdge : legacyEdges) {
            if (deleteEdgeIds.isEmpty()) {
                if (deleteIndex == edgeIndex) {
                    continue;
                }

                if (legacyEdge.connectTargetStation(deleteStation.getId())) {
                    deleteIndex = edgeIndex + 1;

                    Edge nextLegacyEdge = legacyEdges.get(deleteIndex);

                    deleteEdgeIds.add(legacyEdge.getId());
                    deleteEdgeIds.add(nextLegacyEdge.getId());

                    Edge newEdge = createEdge(legacyEdge.getSourceStationId(), nextLegacyEdge.getTargetStationId());
                    sortEdges.add(newEdge);

                    continue;
                }
            }

            sortEdges.add(legacyEdge);

            edgeIndex++;
        }

        for (Long deleteEdgeId : deleteEdgeIds) {
            edgeService.deleteById(deleteEdgeId);
        }

        line.setEdges(sortEdges);
    }

    private Edge createEdge(final long sourceStationId, final long targetStationId) {
        Edge newEdge = Edge.builder()
                .sourceStationId(sourceStationId)
                .targetStationId(targetStationId).build();

        return edgeService.save(newEdge);
    }

    private Set<Long> deleteFirstEdgeOrLastEdge(final List<Edge> legacyEdges, final long deleteStationId) {
        final Set<Long> deleteEdgeIds = new HashSet<>();

        final Edge firstLegacyEdge = legacyEdges.get(0);
        final Edge lastLegacyEdge = legacyEdges.get(legacyEdges.size() - 1);

        if (firstLegacyEdge.connectSourceStation(deleteStationId)) {
            deleteEdgeIds.add(firstLegacyEdge.getId());
            legacyEdges.remove(0);
        } else if (lastLegacyEdge.connectTargetStation(deleteStationId)) {
            deleteEdgeIds.add(lastLegacyEdge.getId());
            legacyEdges.remove(legacyEdges.size() - 1);
        }

        return deleteEdgeIds;
    }
}
