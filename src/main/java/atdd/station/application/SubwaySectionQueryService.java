package atdd.station.application;

import atdd.station.application.dto.SubwayCommonResponseDto;
import atdd.station.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubwaySectionQueryService {
    private SubwaySectionRepository subwaySectionRepository;
    private StationRepository stationRepository;

    public SubwaySectionQueryService(SubwaySectionRepository subwaySectionRepository,
                                     StationRepository stationRepository) {

        this.subwaySectionRepository = subwaySectionRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public List<SubwayCommonResponseDto> getSubwayLines(Long stationId) {
        Station savedStation = stationRepository.findById(stationId)
                .orElseThrow(() -> new IllegalArgumentException("해당 지하철역을 찾을 수 없습니다."));

        List<SubwaySection> subwaySections = subwaySectionRepository.findAllBySourceStationOrTargetStation(savedStation, savedStation);
        return subwaySections.stream()
                .map(subwaySection -> SubwayCommonResponseDto.of(subwaySection.getSubwayLine()))
                .collect(Collectors.toList());
    }

    @Transactional
    public List<SubwaySection> findAllBySubwayLine(SubwayLine subwayLine) {
        return subwaySectionRepository.findBySubwayLine(subwayLine);
    }
}
