package atdd.station.application;

import atdd.station.domain.SubwayLine;
import atdd.station.domain.SubwayLineRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class SubwayLineCommandService {
    private SubwayLineRepository subwayLineRepository;

    public SubwayLineCommandService(SubwayLineRepository subwayLineRepository) {
        this.subwayLineRepository = subwayLineRepository;
    }

    @Transactional
    public SubwayLine create(String subwayLineName) {
        return subwayLineRepository.save(SubwayLine.of(subwayLineName));
    }
}
