package atdd.station.application;

import atdd.station.application.dto.SubwayCommonResponseDto;
import atdd.station.application.dto.SubwayLineResponseDto;
import atdd.station.application.exception.ResourceNotFoundException;
import atdd.station.domain.Station;
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
        List<SubwayLine> subwayLines = findSubwayLines();

        return subwayLines.stream()
                .map(subwayLine -> {
                    List<Station> stations = subwayLine.getStations();

                    List<SubwayCommonResponseDto> subwayCommonResponseDtos = stations.stream()
                            .map(SubwayCommonResponseDto::of)
                            .collect(Collectors.toList());

                    return SubwayLineResponseDto.of(subwayLine, subwayCommonResponseDtos);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public SubwayLineResponseDto getSubwayLine(Long id) {
        SubwayLine savedSubwayLine = findSubwayLineById(id);
        List<Station> stations = savedSubwayLine.getStations();

        List<SubwayCommonResponseDto> subwayCommonResponseDtos = stations.stream()
                .map(SubwayCommonResponseDto::of)
                .collect(Collectors.toList());

        return SubwayLineResponseDto.of(savedSubwayLine, subwayCommonResponseDtos);
    }

    @Transactional
    public SubwayLine findSubwayLineById(Long id) {
        return subwayLineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("해당 지하철 노선을 찾을 수 없습니다."));
    }

    @Transactional
    public List<SubwayLine> findSubwayLines() {
        return subwayLineRepository.findAll();
    }

}
