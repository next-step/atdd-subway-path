package atdd.line;

import atdd.Edge.Edge;
import atdd.Edge.EdgeListResponse;
import atdd.Edge.EdgeRepository;
import atdd.Edge.EdgeService;
import atdd.station.Station;
import atdd.station.StationResponse;
import atdd.station.StationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Service
public class LineService {
    private LineRepository lineRepository;
    private EdgeService edgeService;
    private StationService stationService;

    public LineDetailResponse findLineById(Long id){
        Line line = lineRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 노선입니다 {" + id + "}"));
        EdgeListResponse edges = edgeService.findByLineId(id);
        List<StationResponse> stations = findStations(edges.getEdges());

        LineDetailResponse lineDetail = LineDetailResponse.of(line, stations);
        return lineDetail;

    }

    public List<StationResponse> findStations(List<Edge> edges){
        Map<Long, StationResponse> stations  = new HashMap<>();

        for(Edge edge:edges){
            StationResponse sourceStation = stationService.findById(edge.getSourceStationId());
            StationResponse targetStation = stationService.findById(edge.getTargetStationId());

            stations.put(sourceStation.getId(), sourceStation);
            stations.put(targetStation.getId(), targetStation);

        }

        return new ArrayList<>(stations.values());
    }

    public LineResponse createLine(LineCreateRequest line){
        return LineResponse.of(lineRepository.save(line.toEntity()));
    }


}
