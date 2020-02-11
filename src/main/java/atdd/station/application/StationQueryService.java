package atdd.station.application;

import atdd.station.application.dto.StationResponseDto;
import atdd.station.application.dto.SubwayCommonResponseDto;
import atdd.station.application.exception.ResourceNotFoundException;
import atdd.station.domain.Station;
import atdd.station.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StationQueryService {
    private SubwaySectionQueryService subwaySectionQueryService;
    private StationRepository stationRepository;

    public StationQueryService(SubwaySectionQueryService subwaySectionQueryService,
                               StationRepository stationRepository) {

        this.subwaySectionQueryService = subwaySectionQueryService;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public List<StationResponseDto> getStations() {
        return stationRepository.findAll().stream()
                .map((Station station) -> StationResponseDto.of(station, Collections.EMPTY_LIST))
                .collect(Collectors.toList());
    }

    @Transactional
    public StationResponseDto getStation(Long id) {
        Station savedStation = findStationById(id);
        List<SubwayCommonResponseDto> subwayLines = subwaySectionQueryService.getSubwayLines(savedStation.getId());

        return StationResponseDto.of(savedStation, subwayLines);
    }

    @Transactional
    public Station findStationById(Long id) {
        return stationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 지하철역을 찾을 수 없습니다."));
    }

    @Transactional
    public Station findStationByName(String stationName) {
        return stationRepository.findByName(stationName)
                .orElseThrow(() -> new IllegalArgumentException("해당 지하철역을 찾을 수 없습니다."));
    }
}
