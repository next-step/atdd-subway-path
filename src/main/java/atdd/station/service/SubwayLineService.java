package atdd.station.service;

import atdd.station.domain.SubwayLine;
import atdd.station.domain.SubwayLineRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("subwayLinesService")
public class SubwayLineService {
    @Resource(name = "subwayLineRepository")
    private SubwayLineRepository subwayLineRepository;

    public SubwayLine create(SubwayLine subwayLine) {
        return subwayLineRepository.save(subwayLine);
    }
}
