package atdd.station;

import atdd.Edge.Edge;
import atdd.Edge.EdgeListResponse;
import atdd.Edge.EdgeService;
import atdd.line.Line;
import atdd.line.LineRepository;
import atdd.line.LineResponse;
import atdd.line.LineService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;


@Service
public class StationService {
    private final StationRepository stationRepository;
    private final EdgeService edgeService;
    private final LineService lineService;

    public StationService(StationRepository stationRepository, @Lazy EdgeService edgeService, @Lazy LineService lineService){
        this.stationRepository = stationRepository;
        this.edgeService = edgeService;
        this.lineService = lineService;
    }

    public StationResponse findById(Long id){
        Station station = stationRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 역입니다 {" + id + "}"));
        return StationResponse.of(station);
    }

    public StationDetailResponse findByIdWithLineList(Long id){
        Station station = stationRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 역입니다 {" + id + "}"));
        EdgeListResponse edges = edgeService.findEdgeByStationId(id);
        List<LineResponse> lines = findLines(edges.getEdges());


       return StationDetailResponse.of(station,lines);
    }

    public List<LineResponse> findLines(List<Edge> edges){
        List<LineResponse> lines = new ArrayList<>();
        for(Edge edge: edges){
            LineResponse lineResponse = lineService.findLineById(edge.getLineId());
            lines.add(lineResponse);
        }

        return lines;
    }

    public void deleteStation(Long id){
        stationRepository.deleteById(id);
    }

}
