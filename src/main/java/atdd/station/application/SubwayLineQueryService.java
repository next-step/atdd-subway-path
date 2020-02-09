package atdd.station.application;

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
}
