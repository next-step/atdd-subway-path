package atdd.station.application;

import atdd.station.application.exception.ResourceNotFoundException;
import atdd.station.domain.SubwayLine;
import atdd.station.domain.SubwayLineRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class SubwayLineQueryService {
    private SubwayLineRepository subwayLineRepository;

    public SubwayLineQueryService(SubwayLineRepository subwayLineRepository) {
        this.subwayLineRepository = subwayLineRepository;
    }

    @Transactional
    public List<SubwayLine> getSubwayLines() {
        return subwayLineRepository.findAll();
    }

    @Transactional
    public SubwayLine getSubwayLine(Long id) {
        return subwayLineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("해당 지하철 노선을 찾을 수 없습니다."));
    }
}
