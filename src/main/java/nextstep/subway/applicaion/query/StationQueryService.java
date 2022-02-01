package nextstep.subway.applicaion.query;

import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.exception.station.StationNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class StationQueryService {

    private final StationRepository stationRepository;

    public StationQueryService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public List<StationResponse> findAllStations() {
        List<Station> stations = stationRepository.findAll();

        return stations.stream()
                .map(this::createStationResponse)
                .collect(Collectors.toList());
    }

    public StationResponse showStationById(long id) {
        Station station = findStationsById(id);
        return new StationResponse(station.getId(), station.getName(),
                station.getCreatedDate(), station.getModifiedDate());
    }

    private Station findStationsById(long id) {
        return stationRepository.findById(id)
                .orElseThrow(() -> new StationNotFoundException(id));
    }

    public StationResponse createStationResponse(Station station) {
        return new StationResponse(
                station.getId(),
                station.getName(),
                station.getCreatedDate(),
                station.getModifiedDate()
        );
    }

}
