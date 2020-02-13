package atdd.Edge;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class EdgeService {
    private EdgeRepository edgeRepository;

    public EdgeListResponse findByLineId(Long lineId){
        return EdgeListResponse.of(edgeRepository.findByLineId(lineId));
    }
}
