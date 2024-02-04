package nextstep.subway.station.service;

import nextstep.subway.station.exception.StationNotExistException;
import nextstep.subway.station.repository.StationRepository;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.service.dto.StationRequest;
import nextstep.subway.station.service.dto.StationResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class StationServiceImpl implements StationService, StationProvider {
    private final StationRepository stationRepository;

    public StationServiceImpl(final StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    @Override
    @Transactional
    public StationResponse saveStation(final StationRequest stationRequest) {
        final Station station = stationRepository.save(new Station(stationRequest.getName()));
        return StationResponse.from(station);
    }

    @Override
    public List<StationResponse> findAllStations() {
        return stationRepository.findAll().stream()
                .map(StationResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteStationById(final Long id) {
        stationRepository.deleteById(id);
    }

    @Override
    public Station findById(final Long id) {
        return stationRepository.findById(id).orElseThrow(() -> new StationNotExistException(id));
    }
}
