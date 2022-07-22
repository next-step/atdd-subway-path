package nextstep.subway.station.applicaion;

import lombok.RequiredArgsConstructor;
import nextstep.subway.station.applicaion.dto.request.StationRequest;
import nextstep.subway.station.applicaion.dto.response.StationResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StationService {
    private final StationRepository stationRepository;

    @Transactional
    public StationResponse saveStation(StationRequest stationRequest) {
        Station station = stationRepository.save(new Station(stationRequest.getName()));
        return createStationResponse(station);
    }

    public List<StationResponse> findAllStations() {
        return stationRepository.findAll().stream()
                .map(this::createStationResponse)
                .collect(Collectors.toList());
    }

    public List<StationResponse> findByIds(List<Long> ids) {
        return stationRepository.findByIdIn(ids).stream()
                .map(this::createStationResponse)
                .collect(Collectors.toList());
    }

    public StationResponse createStationResponse(Station station) {
        return new StationResponse(
                station.getId(),
                station.getName()
        );
    }

    @Transactional
    public void deleteStationById(Long id) {
        stationRepository.deleteById(id);
    }

    public Station findById(Long id) {
        return stationRepository.findById(id).orElseThrow(IllegalArgumentException::new);
    }
}
