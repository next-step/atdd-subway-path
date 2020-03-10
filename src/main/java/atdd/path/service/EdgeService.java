package atdd.path.service;

import atdd.path.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EdgeService {
    private EdgeRepository edgeRepository;
    private LineService lineService;
    private StationService stationService;

    public EdgeService(EdgeRepository edgeRepository, LineService lineService, StationService stationService) {
        this.edgeRepository = edgeRepository;
        this.lineService = lineService;
        this.stationService = stationService;
    }

    public Edge addEdge(Long lineId, Long sourceId, Long targetId, int distance) {
        Line line = lineService.findById(lineId);
        Station source = stationService.findById(sourceId);
        Station target = stationService.findById(targetId);

        Edge edge = Edge.builder()
                .line(line)
                .source(source)
                .target(target)
                .distance(10)
                .build();
        Edge save = edgeRepository.save(edge);
        return save;
    }

    public Line mergeEdgeByStationId(Long lineId, Long stationId) {
        Line line = lineService.findById(lineId);
        Station stationToDelete = stationService.findById(stationId);
        Optional<Station> sourceStation = line.getEdges().findSourceStation(stationToDelete);
        Optional<Station> targetStation = line.getEdges().findTargetStation(stationToDelete);
        List<Long> idOfEdgesToDelete = line.findIdOfEdgesToDelete(stationToDelete);

        int newDistance = 0;
        for (Long id : idOfEdgesToDelete) {
            edgeRepository.deleteById(id);
            Edge oldEdge = edgeRepository.findById(id).orElseThrow(RuntimeException::new);
            newDistance= newDistance+oldEdge.getDistance();
        }

        if(targetStation.isPresent() && sourceStation.isPresent()){
            Edge newEdge = Edge.builder()
                    .line(line)
                    .source(sourceStation.get())
                    .target(targetStation.get())
                    .distance(newDistance)
                    .build();
            Edge save = edgeRepository.save(newEdge);
            line.getEdges().addEdge(save);
        }
        return line;
    }

    public Edges deleteEdgeByStationId(Long lineId, Long stationId) {
        Line line = lineService.findById(lineId);
        Station stationToDelete = stationService.findById(stationId);
        List<Long> idOfEdgesToDelete = line.findIdOfEdgesToDelete(stationToDelete);

        int newDistance = 0;
        for (Long id : idOfEdgesToDelete) {
            Edge oldEdge = edgeRepository.findById(id).orElseThrow(RuntimeException::new);
            newDistance= newDistance+oldEdge.getDistance();
            edgeRepository.deleteById(id);
        }
        Edges edgesAfterRemovalStation = line.findNewEdges(stationToDelete);
        line.changeEdges(edgesAfterRemovalStation);
        return edgesAfterRemovalStation;
    }
}
