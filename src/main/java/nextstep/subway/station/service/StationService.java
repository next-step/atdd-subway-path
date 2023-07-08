package nextstep.subway.station.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import subway.station.domain.Station;
import subway.station.exception.StationNotFoundException;
import subway.station.repository.StationRepository;
import subway.station.view.StationRequest;
import subway.station.view.StationResponse;

@Service
@Transactional(readOnly = true)
public class StationService {
    private StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

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

    @Transactional
    public void deleteStationById(Long id) {
        stationRepository.deleteById(id);
    }

    private StationResponse createStationResponse(Station station) {
        return new StationResponse(
                station.getId(),
                station.getName()
        );
    }

    @Transactional(readOnly = true)
    public Optional<Station> findById(Long id) {
        return stationRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Station get(Long id) {
        return stationRepository.findById(id)
                                .orElseThrow(StationNotFoundException::new);
    }
}
