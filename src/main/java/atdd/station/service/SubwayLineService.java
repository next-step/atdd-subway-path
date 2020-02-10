package atdd.station.service;

import atdd.station.domain.SubwayLine;
import atdd.station.domain.SubwayLineRepository;
import atdd.station.dto.subwayLine.SubwayLineCreateRequestDto;
import atdd.station.dto.subwayLine.SubwayLineDetailResponseDto;
import atdd.station.dto.subwayLine.SubwayLineListResponseDto;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("subwayLineService")
public class SubwayLineService {
    @Resource(name = "subwayLineRepository")
    private SubwayLineRepository subwayLineRepository;

    public SubwayLine create(SubwayLineCreateRequestDto subwayLine) {
        return subwayLineRepository.save(subwayLine.toEntity());
    }

    public SubwayLineListResponseDto list() {
        return SubwayLineListResponseDto.toDtoEntity(subwayLineRepository.findAll());
    }

    public SubwayLine findById(long id) {
        return subwayLineRepository.findById(id).orElseThrow(IllegalAccessError::new);
    }

    public SubwayLineDetailResponseDto detail(long id) {
        return SubwayLineDetailResponseDto.toDtoEntity(findById(id));
    }

    public void delete(long defaultId) {
        findById(defaultId).deleteSubwayLine();
    }
}
