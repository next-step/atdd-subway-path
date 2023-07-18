package nextstep.subway.service;

import nextstep.subway.common.NotFoundStationException;
import nextstep.subway.controller.resonse.StationResponse;
import nextstep.subway.domain.Station;
import nextstep.subway.repository.StationRepository;
import nextstep.subway.domain.command.StationCreateCommand;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public StationResponse saveStation(StationCreateCommand command) {
        Station station = stationRepository.save(Station.create(command));
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

    public StationResponse findStation(Long id) {
        Station station = requireGetById(id);
        return this.createStationResponse(station);
    }

    private Station requireGetById(Long id) {
        return stationRepository.findById(id)
                .orElseThrow(() -> new NotFoundStationException(id));
    }

    private StationResponse createStationResponse(Station station) {
        return new StationResponse(
                station.getId(),
                station.getName()
        );
    }
}
