package atdd.station.service;

import atdd.exception.ErrorType;
import atdd.exception.SubwayException;
import atdd.station.model.entity.Edge;
import atdd.station.model.entity.Line;
import atdd.station.model.entity.Station;
import atdd.station.repository.LineRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

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

    public List<Line> findAllById(List<Long> ids) {
        return lineRepository.findAllById(ids);
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

        newEdge.connectedOf(legacyEdges);
        newEdge = edgeService.save(newEdge);

        List<Edge> edges = line.sortEdge(legacyEdges, newEdge);
        line.setEdges(edgeService.saveAll(edges));

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

    public void deleteStation(final long id, final long stationId) {
        final Line line = findById(id);
        final Station deleteStation = stationService.findById(stationId);
        final List<Edge> legacyEdges = edgeService.findAllById(line.getEdgeIds());

        // 노선에서 구간 삭제
        List<Edge> deletedEdges = deleteEdges(legacyEdges, deleteStation);

        Optional<Edge> combineEdgeOptional = combineEdge(deletedEdges);

        List<Edge> newEdges = exceptEdge(legacyEdges, deletedEdges);

        if (combineEdgeOptional.isPresent()) {
            Edge newEdge = edgeService.save(combineEdgeOptional.get());
            newEdges = line.sortEdge(newEdges, newEdge);
        }

        line.setEdges(newEdges);
        lineRepository.save(line);

        // 지하철역에서 라인 삭제
        deleteLineInStation(deleteStation, line.getId());
    }

    private List<Edge> deleteEdges(List<Edge> legacyEdges, Station deleteStation) {
        List<Edge> deleteEdges = findConnectedEdge(legacyEdges, deleteStation);
        edgeService.deleteAll(deleteEdges);

        return deleteEdges;
    }

    private Optional<Edge> combineEdge(List<Edge> edges) {
        if (edges.size() > 1) {
            int distance = 0;
            int elapsedTime = 0;

            for (Edge edge : edges) {
                distance += edge.getDistance();
                elapsedTime += edge.getElapsedTime();
            }

            Edge newEdge = new Edge(edges.get(0).getSourceStationId(),
                    edges.get(edges.size() - 1).getTargetStationId(),
                    elapsedTime,
                    distance);

            return Optional.of(newEdge);
        }

        return Optional.empty();
    }

    private List<Edge> exceptEdge(List<Edge> edges, List<Edge> exceptEdges) {
        List<Edge> result = new ArrayList<>();

        for (Edge edge : edges) {
            boolean isRemove = isFindEdge(exceptEdges, edge);

            if (!isRemove) {
                result.add(edge);
            }
        }

        return result;
    }

    private boolean isFindEdge(List<Edge> edges, Edge edge) {
        return edges.stream().anyMatch(it -> it.getId() == edge.getId());
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

    private List<Edge> findConnectedEdge(List<Edge> edges, final Station station) {
        return edges.stream().filter(it -> it.connectSourceStation(station.getId()) || it.connectTargetStation(station.getId()))
                .collect(Collectors.toList());
    }
}
