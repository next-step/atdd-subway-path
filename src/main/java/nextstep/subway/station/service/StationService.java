package nextstep.subway.station.service;

import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import nextstep.subway.station.entity.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import nextstep.subway.station.repository.StationRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class StationService {
    private StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    @Transactional
    public StationResponse saveStation(final StationRequest stationRequest) {
        final Station station = stationRepository.save(new Station(stationRequest.getName()));

        return createStationResponse(station);
    }

    public List<StationResponse> findAllStations() {
        return stationRepository.findAll().stream()
                .map(this::createStationResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteStationById(final Long id) {
        stationRepository.deleteById(id);
    }

    private StationResponse createStationResponse(final Station station) {
        return new StationResponse(
                station.getId(),
                station.getName()
        );
    }
}
