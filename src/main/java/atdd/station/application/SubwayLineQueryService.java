package atdd.station.application;

import atdd.station.application.dto.SubwayLineResponseDto;
import atdd.station.application.exception.ResourceNotFoundException;
import atdd.station.domain.SubwayLine;
import atdd.station.domain.SubwayLineRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubwayLineQueryService {
    private SubwayLineRepository subwayLineRepository;

    public SubwayLineQueryService(SubwayLineRepository subwayLineRepository) {
        this.subwayLineRepository = subwayLineRepository;
    }

    @Transactional
    public List<SubwayLineResponseDto> getSubwayLines() {
        List<SubwayLine> subwayLines = subwayLineRepository.findAll();

        return subwayLines.stream()
                .map(SubwayLineResponseDto::of)
                .collect(Collectors.toList());
    }

    @Transactional
    public SubwayLineResponseDto getSubwayLine(Long id) {
        return SubwayLineResponseDto.of(findSubwayLineById(id));
    }

    @Transactional
    public SubwayLine findSubwayLineById(Long id) {
        return subwayLineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("해당 지하철 노선을 찾을 수 없습니다."));
    }

}
