package atdd.station.service;

import atdd.station.model.Graph;
import atdd.station.model.entity.Station;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PathService {
    @Autowired
    private EdgeService edgeService;

    @Autowired
    private StationService stationService;

    public List<Station> findPath(long startId, long endId) {
        Graph graph = new Graph(edgeService.findAll());

        List<Long> pathStationIds = graph.shortestDistancePath(startId, endId);

        List<Station> stations = pathStationIds.stream()
                .map(it -> stationService.findById(it))
                .collect(Collectors.toList());

        return stations;
    }

    public List<Station> findShortTimePath(long startId, long endId) {
        Graph graph = new Graph(edgeService.findAll());

        List<Long> pathStationIds = graph.shortestTimePath(startId, endId);

        List<Station> stations = pathStationIds.stream()
                .map(it -> stationService.findById(it))
                .collect(Collectors.toList());

        return stations;
    }
}
