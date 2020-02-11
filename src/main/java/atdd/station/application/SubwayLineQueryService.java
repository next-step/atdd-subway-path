package atdd.station.application;

import atdd.station.application.dto.SubwayCommonResponseDto;
import atdd.station.application.dto.SubwayLineResponseDto;
import atdd.station.application.exception.ResourceNotFoundException;
import atdd.station.domain.Station;
import atdd.station.domain.SubwayLine;
import atdd.station.domain.SubwayLineRepository;
import atdd.station.domain.SubwaySection;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class SubwayLineQueryService {
    private SubwaySectionQueryService subwaySectionQueryService;
    private SubwayLineRepository subwayLineRepository;

    public SubwayLineQueryService(SubwaySectionQueryService subwaySectionQueryService,
                                  SubwayLineRepository subwayLineRepository) {

        this.subwaySectionQueryService = subwaySectionQueryService;
        this.subwayLineRepository = subwayLineRepository;
    }

    @Transactional
    public List<SubwayLineResponseDto> getSubwayLines() {
        List<SubwayLine> subwayLines = subwayLineRepository.findAll();

        return subwayLines.stream()
                .map(subwayLine -> SubwayLineResponseDto.of(subwayLine, getStationsFromSubwayLine(subwayLine)))
                .collect(Collectors.toList());
    }

    private List<SubwayCommonResponseDto> getStationsFromSubwayLine(SubwayLine subwayLine) {
        List<SubwaySection> savedSubwaySections = subwaySectionQueryService.findAllBySubwayLineId(subwayLine.getId());
        List<Station> savedStations = getStationsFromSubwaySection(savedSubwaySections);

        return savedStations.stream()
                .map(SubwayCommonResponseDto::of)
                .collect(Collectors.toList());
    }

    private List<Station> getStationsFromSubwaySection(List<SubwaySection> savedSubwaySections) {
        List<Station> stations = Collections.emptyList();
        savedSubwaySections.forEach(subwaySection -> {
            Station sourceStation = subwaySection.getSourceStation();
            stations.add(sourceStation);

            Station targetStation = subwaySection.getTargetStation();

            SubwaySection nextSubwaySection = savedSubwaySections.stream()
                    .filter(section -> section.getSourceStation().equals(targetStation)
                    ).findFirst().orElse(null);

            if (Objects.nonNull(nextSubwaySection)) {
                stations.add(nextSubwaySection.getSourceStation());
            }
        });

        return stations;
    }

    @Transactional
    public SubwayLineResponseDto getSubwayLine(Long id) {
        SubwayLine savedSubwayLine = findSubwayLineById(id);
        return SubwayLineResponseDto.of(findSubwayLineById(id), getStationsFromSubwayLine(savedSubwayLine));
    }

    @Transactional
    public SubwayLine findSubwayLineById(Long id) {
        return subwayLineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("해당 지하철 노선을 찾을 수 없습니다."));
    }

}
