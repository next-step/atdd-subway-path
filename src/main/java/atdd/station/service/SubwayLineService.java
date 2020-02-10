package atdd.station.service;

import atdd.station.domain.SubwayLine;
import atdd.station.domain.SubwayLineRepository;
import atdd.station.dto.subwayLine.SubwayLineCreateRequestDto;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service("subwayLineService")
public class SubwayLineService {
    @Resource(name = "subwayLineRepository")
    private SubwayLineRepository subwayLineRepository;

    public SubwayLine create(SubwayLineCreateRequestDto subwayLine) {
        return subwayLineRepository.save(subwayLine.toEntity());
    }

    public List<SubwayLine> list() {
        return subwayLineRepository.findAll();
    }
}
