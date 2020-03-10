package atdd.path.service;

import atdd.path.application.dto.LineRequestView;
import atdd.path.application.dto.LineResponseView;
import atdd.path.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

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

    public void deleteEdgeByStationId(Long lineId, Long stationId) {
        Line line = lineService.findById(lineId);
        Station stationToDelete = stationService.findById(stationId);
        List<Long> idOfEdgesToDelete = line.findIdOfEdgesToDelete(stationToDelete);
        for (Long id : idOfEdgesToDelete) {
            edgeRepository.deleteById(id);
        }

        Edges newEdges = line.findNewEdges(stationToDelete);
        line.changeEdges(newEdges);
    }
}
