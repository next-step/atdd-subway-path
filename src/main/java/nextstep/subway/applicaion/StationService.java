package nextstep.subway.applicaion;

import lombok.RequiredArgsConstructor;
import nextstep.subway.applicaion.dto.SectionDto;
import nextstep.subway.applicaion.dto.StationDto;
import nextstep.subway.applicaion.dto.request.StationRequest;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StationService {
    private final StationRepository stationRepository;

    @Transactional
    public StationDto saveStation(StationRequest stationRequest) {
        Station station = stationRepository.save(new Station(stationRequest.getName()));
        return createStationDto(station);
    }

    public List<StationDto> findAllStations() {
        return createStationDto(stationRepository.findAll());
    }

    @Transactional
    public void deleteStationById(Long id) {
        stationRepository.deleteById(id);
    }

    public StationDto createStationDto(Station station) {
        return StationDto.from(station);
    }

    public List<StationDto> createStationDto(List<Station> station) {
        return StationDto.from(station);
    }

    public Station findById(Long id) {
        return stationRepository.findById(id).orElseThrow(IllegalArgumentException::new);
    }
}
