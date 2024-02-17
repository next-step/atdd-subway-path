package nextstep.subway.station.service;

import nextstep.subway.exception.NotFoundLineException;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.presentation.request.CreateStationRequest;
import nextstep.subway.station.presentation.response.CreateStationResponse;
import nextstep.subway.station.presentation.response.ShowAllStationsResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class StationService {

    private StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public Station findById(Long StationId) {
        return stationRepository.findById(StationId)
                .orElseThrow(() -> new NotFoundLineException());
    }

    @Transactional
    public CreateStationResponse saveStation(CreateStationRequest createStationRequest) {
        Station station = stationRepository.save(Station.from(createStationRequest.getName()));
        return CreateStationResponse.from(station);
    }

    public ShowAllStationsResponse findAllStations() {
        return ShowAllStationsResponse.from(stationRepository.findAll().stream()
                .map(StationDto::from)
                .collect(Collectors.toList()));
    }

    @Transactional
    public void deleteStationById(Long stationId) {
        stationRepository.deleteById(stationId);
    }

}
