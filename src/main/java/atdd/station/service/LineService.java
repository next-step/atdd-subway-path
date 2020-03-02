package atdd.station.service;

import atdd.exception.ErrorType;
import atdd.exception.SubwayException;
import atdd.station.model.entity.Edge;
import atdd.station.model.entity.Line;
import atdd.station.model.entity.Station;
import atdd.station.repository.LineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    // TODO 테스트 코드 작성
    public Line addEdge(final long id, Edge newEdge) {
        final Line line = findById(id);
        final List<Edge> legacyEdges = edgeService.findAllById(line.getEdgeIds());

        newEdge = edgeService.createEdge(legacyEdges, newEdge);
        line.setLineEdges(legacyEdges, newEdge);

        // update station
        final Set<Long> stationIds = stationIdsInEdges(line.getLineEdges());
        List<Station> stationList = stationService.updateLine(stationIds, line.getId());

        line.setLineStations(stationList);

        return lineRepository.save(line);
    }

    private Set<Long> stationIdsInEdges(final List<Edge> edges) {
        final Set<Long> stationIds = new HashSet<>();

        edges.stream().forEach(data -> {
            stationIds.addAll(data.stationIds());
        });

        return stationIds;
    }

    // TODO 리팩토링 , 테스트 코드 작성
    public void deleteStationInLine(final long id, final long stationId) {
        final Line line = findById(id);
        final Station deleteStation = stationService.findById(stationId);

        // 노선에서 구간 삭제
        deleteEdgeInLine(line, deleteStation);
        lineRepository.save(line);

        // 지하철역에서 라인 삭제
        deleteLineInStation(deleteStation, line.getId());
    }

    // TODO 리팩토링 테스트 코드 작성
    private void deleteEdgeInLine(final Line line, final Station deleteStation) {
        final List<Edge> legacyEdges = edgeService.findAllById(line.getEdgeIds());

        final List<Edge> newEdges = new ArrayList<>();
        final Set<Long> newStationIds = new HashSet<>();

        int edgeIndex = 0;
        int deleteIndex = -1;
        for (Edge legacyEdge : legacyEdges) {
            if (deleteIndex > 0 && deleteIndex == edgeIndex) {
                deleteIndex = -1;

                edgeService.deleteById(legacyEdge.getId());
                continue;
            }

            if (legacyEdge.connectSourceStation(deleteStation.getId()) || legacyEdge.connectTargetStation(deleteStation.getId())) {
                if (newEdges.isEmpty() && legacyEdge.connectSourceStation(deleteStation.getId())) {
                    edgeService.deleteById(legacyEdge.getId());
                    continue;
                }

                if (edgeIndex == (legacyEdges.size() - 1) && legacyEdge.connectTargetStation(deleteStation.getId())) {
                    edgeService.deleteById(legacyEdge.getId());
                    continue;
                }

                if (legacyEdge.connectTargetStation(deleteStation.getId())) {
                    deleteIndex = edgeIndex + 1;

                    Edge nextLegacyEdge = legacyEdges.get(deleteIndex);
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
    }

    public List<Edge> getLineEdges(Line line) {
        List<Edge> edges = line.getLineEdges();

        if (edges.isEmpty()) {
            return edgeService.findAllById(line.getEdgeIds());
        }

        return edges;
    }

    public List<Station> getLineStations(Line line, List<Edge> edges) {
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
