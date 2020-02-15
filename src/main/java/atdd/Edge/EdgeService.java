package atdd.Edge;

import atdd.station.StationService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class EdgeService {
    private EdgeRepository edgeRepository;
    private StationService stationService;

    public EdgeService(EdgeRepository edgeRepository, @Lazy StationService stationService){
        this.edgeRepository = edgeRepository;
        this.stationService = stationService;
    }

    public EdgeResponse createEdge(EdgeCreateRequest edge){
        Edge savedEdge = edgeRepository.save(edge.toEntity());
        return EdgeResponse.of(savedEdge);
    }

    public EdgeListResponse findByLineId(Long lineId){
        return EdgeListResponse.of(edgeRepository.findByLineId(lineId));
    }

    public EdgeListResponse findEdgeByStationId(Long stationId){
        return EdgeListResponse.of(edgeRepository.findByStationId(stationId));
    }

    public void deleteEdge(Long stationId){
        EdgeListResponse edgeListResponse = findEdgeByStationId(stationId);
        List<Edge> edges = edgeListResponse.getEdges();
        HashMap<Long, Edge> edgeMapBySourceStationId = new HashMap<>();

        for(Edge edge:edges){edgeMapBySourceStationId.put(edge.getSourceStationId(), edge);}

        Set<Edge> edgeSet = new HashSet<>(edges);
        Iterator<Edge> iterator = edgeSet.iterator();

        while(iterator.hasNext()){
            Edge sourceEdge = iterator.next();
            //checkStationID
            if(sourceEdge.getTargetStationId().equals(stationId)){
                Edge targetEdge = edgeMapBySourceStationId.get(sourceEdge.getTargetStationId());

                Long lineId = sourceEdge.getLineId();
                int elapsedTime = sourceEdge.getElapsedTime() + targetEdge.getElapsedTime();
                BigDecimal distance = sourceEdge.getDistance().add(targetEdge.getDistance());
                Long sourceStationId = sourceEdge.getSourceStationId();
                Long targetStationId = targetEdge.getTargetStationId();

                EdgeCreateRequest newEdge = EdgeCreateRequest.builder()
                        .lineId(lineId)
                        .elapsedTime(elapsedTime)
                        .distance(distance)
                        .sourceStationId(sourceStationId)
                        .targetStationId(targetStationId)
                        .build();

                stationService.deleteStation(stationId);
                deleteRealEdge(sourceEdge.getId());
                deleteRealEdge(targetEdge.getId());

                createEdge(newEdge);
            }
        }
    }

    public void deleteRealEdge(Long id){
        edgeRepository.deleteById(id);
    }
}
