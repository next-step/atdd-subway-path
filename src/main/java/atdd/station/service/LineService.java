package atdd.station.service;

import atdd.station.model.entity.Edge;
import atdd.station.model.entity.Line;
import atdd.station.model.entity.Station;
import atdd.station.repository.EdgeRepository;
import atdd.station.repository.LineRepository;
import atdd.station.repository.StationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class LineService {
    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private EdgeRepository edgeRepository;

    public Line create(final Line line) {
        return lineRepository.save(line);
    }

    public List<Line> findAll() {
        return lineRepository.findAll();
    }

    public Optional<Line> findById(final long id) {
        return lineRepository.findById(id);
    }

    public void deleteById(final long id) {
        lineRepository.deleteById(id);
    }

    public void stationDtos(final Line line) {
        List<Edge> edges = edgeRepository.findAllById(line.getEdgeIds());

        Set<Long> stationIds = new HashSet<>();
        edges.forEach(data -> stationIds.addAll(data.stationIds()));
        List<Station> stations = stationRepository.findAllById(stationIds);

        line.setStationDtos(edges, stations);
    }

    public Optional<Line> addEdge(final long id, final Edge newEdge) {
        final Line line = lineRepository.findById(id).get();
        final List<Edge> legacyEdges = edgeRepository.findAllById(line.getEdgeIds());

        final List<Edge> newEdges = new ArrayList<>();
        final Set<Long> stationIds = new HashSet<>();

        boolean isConnect = legacyEdges.stream().anyMatch(data -> data.connectedEdge(newEdge));

        if (!legacyEdges.isEmpty() && !isConnect)
            return Optional.ofNullable(null);

        edgeRepository.save(newEdge);

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

        return Optional.of(lineRepository.save(line));
    }

    public Optional<Line> deleteEdge(final long id, final long stationId) {
        final Line line = lineRepository.findById(id).get();
        final Station station = stationRepository.findById(stationId).get();

        // 노선에서 구간 삭제
        final List<Edge> legacyEdges = edgeRepository.findAllById(line.getEdgeIds());

        final List<Edge> newEdges = new ArrayList<>();
        final Set<Long> newStationIds = new HashSet<>();

        int edgeIndex = 0;
        int continueIndex = -1;
        for (Edge legacyEdge : legacyEdges) {
            if (continueIndex > 0 && continueIndex == edgeIndex) {
                continueIndex = -1;

                edgeRepository.delete(legacyEdge);
                continue;
            }

            if (legacyEdge.getSourceStationId() == station.getId() || legacyEdge.getTargetStationId() == station.getId()) {
                if (newEdges.isEmpty() && legacyEdge.getSourceStationId() == station.getId()) {
                    edgeRepository.delete(legacyEdge);
                    continue;
                }

                if (edgeIndex == (legacyEdges.size() - 1) && legacyEdge.getTargetStationId() == station.getId()) {
                    edgeRepository.delete(legacyEdge);
                    continue;
                }

                if (legacyEdge.getTargetStationId() == station.getId()) {
                    continueIndex = edgeIndex + 1;

                    Edge nextLegacyEdge = legacyEdges.get(continueIndex);
                    Edge newEdge = Edge.builder()
                            .sourceStationId(legacyEdge.getSourceStationId())
                            .targetStationId(nextLegacyEdge.getTargetStationId()).build();

                    edgeRepository.save(newEdge);

                    newEdges.add(newEdge);
                    newStationIds.add(newEdge.getSourceStationId());
                    newStationIds.add(newEdge.getTargetStationId());
                }

                edgeRepository.delete(legacyEdge);
                continue;
            }

            newEdges.add(legacyEdge);
            newStationIds.add(legacyEdge.getSourceStationId());
            newStationIds.add(legacyEdge.getTargetStationId());

            edgeIndex++;
        }

        List<Station> stationList = stationRepository.findAllById(newStationIds);

        line.updateEdge(newEdges, stationList);
        lineRepository.save(line);

        // 지하철역에서 라인 삭제
        deleteLineInStation(station, line.getId());

        return Optional.ofNullable(line);
    }

    private List<Station> updateLineInStations(final Set<Long> stationIds, final long lineId) {
        List<Station> stationList = stationRepository.findAllById(stationIds);

        for (Station station : stationList) {
            Set<Long> lineIds = new HashSet<>(station.getLineIds());
            lineIds.add(lineId);

            station.setLineIds(new ArrayList<>(lineIds));
        }

        return stationRepository.saveAll(stationList);
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

        return stationRepository.save(station);
    }
}
