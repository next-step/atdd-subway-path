package nextstep.subway.station.application;

import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import nextstep.subway.station.exception.StationAlreadyExistException;
import nextstep.subway.station.exception.StationNonExistException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.station.exception.StationExceptionMessage.EXCEPTION_MESSAGE_EXIST_STATION;
import static nextstep.subway.station.exception.StationExceptionMessage.EXCEPTION_MESSAGE_NON_EXIST_STATION;

@Service
@Transactional
public class StationService {

    private final StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public StationResponse saveStation(StationRequest stationRequest) {
        validateStationName(stationRequest.getName());

        Station savedStation = stationRepository.save(stationRequest.toStation());
        return StationResponse.of(savedStation);
    }

    private void validateStationName(String stationName) {
        if (stationRepository.existsByName(stationName)) {
            throw new StationAlreadyExistException(EXCEPTION_MESSAGE_EXIST_STATION);
        }
    }

    @Transactional(readOnly = true)
    public List<StationResponse> findAllStations() {
        List<Station> stations = stationRepository.findAll();

        return stations.stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());
    }

    public void deleteStationById(Long id) {
        stationRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Station findStationById(Long stationId) {
        return stationRepository.findById(stationId)
                .orElseThrow(() -> new StationNonExistException(EXCEPTION_MESSAGE_NON_EXIST_STATION));
    }
}
